# Chapter 33: Design a File Storage System (Dropbox / Google Drive)

## 1. Problem Statement

Design a cloud file storage and synchronization service like Dropbox or Google Drive
that allows users to upload, download, and sync files across multiple devices. The system
must handle file versioning, sharing with permissions, conflict resolution, efficient
delta synchronization, and real-time notifications when files change.

---

## 2. Functional Requirements

| ID | Requirement | Description |
|----|-------------|-------------|
| FR1 | Upload/Download | Upload and download files from any device |
| FR2 | File Sync | Automatically sync files across all user's devices |
| FR3 | File Sharing | Share files/folders with other users or via public link |
| FR4 | Versioning | Maintain file history, restore previous versions |
| FR5 | Notifications | Notify other devices when files change |
| FR6 | Offline Edit | Edit files offline, sync when back online |
| FR7 | Folder Structure | Support hierarchical folder organization |
| FR8 | Conflict Resolution | Handle conflicts when same file edited on two devices |

## 3. Non-Functional Requirements

| ID | Requirement | Target |
|----|-------------|--------|
| NFR1 | Scale | 500M registered users, 10M DAU |
| NFR2 | Storage | Average 200 files/user, average 500KB/file |
| NFR3 | Sync Latency | Changes reflected on other devices within 5 seconds |
| NFR4 | Reliability | Zero data loss (99.999999999% durability, 11 nines) |
| NFR5 | Availability | 99.9% uptime |
| NFR6 | Bandwidth | Minimize bandwidth usage (delta sync, dedup) |
| NFR7 | Concurrent | Support concurrent edits with conflict detection |

---

## 4. Capacity Estimation

### 4.1 Storage

```
Total files: 500M users * 200 files = 100 billion files
Average file size: 500 KB
Total storage: 100B * 500KB = 50 PB (petabytes)

With versioning (avg 3 versions per file, but shared chunks):
  ~75-100 PB total (chunk-level dedup reduces this significantly)

Daily new/modified files:
  10M DAU * 5 file changes/day = 50M file operations/day
  = ~580 operations/sec
  Peak: ~2,000 operations/sec
```

### 4.2 Bandwidth

```
Upload: 50M file changes/day * 500KB avg = 25 TB/day upload
  = ~290 MB/sec sustained
  Peak: ~1 GB/sec

Download: Assume 3x read amplification (sync to 3 devices per user)
  = 75 TB/day download = ~870 MB/sec
  Peak: ~3 GB/sec

With delta sync (only changed chunks):
  Actual bandwidth reduced by 50-80%
  Effective: ~150 MB/sec upload, ~400 MB/sec download
```

### 4.3 Metadata

```
Metadata per file: ~500 bytes (path, size, hash, version, permissions)
100B files * 500 bytes = 50 TB metadata
  (Fits in a sharded database cluster)

Chunk metadata per file: avg 10 chunks * 100 bytes = 1 KB per file
100B files * 1 KB = 100 TB chunk metadata
```

---

## 5. File Upload: Chunked Upload

### 5.1 Why Chunking?

```
Split every file into fixed-size chunks (typically 4 MB):

Benefits:
  1. RESUMABLE UPLOAD: If upload fails at chunk 7, resume from chunk 7
     (don't re-upload the entire 100MB file)
  
  2. DEDUPLICATION: Two files sharing identical chunks only store once
     (content-addressable by SHA-256 hash)
  
  3. DELTA SYNC: When a file is edited, only changed chunks are re-uploaded
     (not the entire file)
  
  4. PARALLEL UPLOAD: Upload multiple chunks simultaneously
     (faster for large files)
  
  5. VERSIONING EFFICIENCY: New version shares unchanged chunks with old version
     (massive storage savings)

Example:
  File: report.pdf (12 MB)
  
  Split into chunks:
    Chunk 0: bytes[0 - 4MB]       → SHA256: "abc123..."
    Chunk 1: bytes[4MB - 8MB]     → SHA256: "def456..."
    Chunk 2: bytes[8MB - 12MB]    → SHA256: "ghi789..."
  
  File edited (only page 3 changed, which is in chunk 1):
    Chunk 0: unchanged             → SHA256: "abc123..." (SKIP upload)
    Chunk 1: NEW content           → SHA256: "xyz999..." (UPLOAD this)
    Chunk 2: unchanged             → SHA256: "ghi789..." (SKIP upload)
  
  Result: Only 4MB uploaded instead of 12MB!
```

### 5.2 Chunking Algorithm

```
Approach 1: Fixed-size chunks (4 MB)
  - Simple: split at fixed byte boundaries
  - Problem: inserting a byte at the start shifts ALL chunk boundaries
  - All chunks become "new" even though content barely changed

Approach 2: Content-defined chunking (Rabin fingerprinting)
  - Use a rolling hash to find "natural" chunk boundaries
  - Boundaries depend on content, not position
  - Inserting bytes only affects 1-2 chunks
  - Better delta sync efficiency
  
  Rabin fingerprint:
    Slide a 48-byte window over the file
    Compute rolling hash at each position
    When hash matches a pattern (e.g., low 13 bits == 0):
      → Mark as chunk boundary
    Average chunk size: ~4 MB (tuned by pattern)
    Min chunk: 512 KB, Max chunk: 8 MB (clamped)
```

---

## 6. Block Storage Service

### 6.1 Content-Addressable Storage

```
Chunks are stored by their SHA-256 hash (content-addressable):

  chunk_hash = SHA256(chunk_data)
  storage_path = /blocks/{chunk_hash[0:2]}/{chunk_hash[2:4]}/{chunk_hash}

  Example:
    Hash: "a1b2c3d4e5f6..."
    Path: /blocks/a1/b2/a1b2c3d4e5f6...

Benefits:
  - Automatic deduplication: same content → same hash → stored once
  - Verification: download chunk, recompute hash, verify integrity
  - Immutable: chunks never modified, only created or garbage-collected

Storage Backend: Amazon S3 (or equivalent object storage)
  - 11 nines of durability
  - Automatic replication across AZs
  - Cost-effective at petabyte scale
```

### 6.2 Upload Flow

```
  Client                   API Server          Block Service         S3
    |                          |                    |                  |
    |--split file into chunks->|                    |                  |
    |  compute SHA256 per chunk|                    |                  |
    |                          |                    |                  |
    |--"upload request"------->|                    |                  |
    |  {filename, chunks:[     |                    |                  |
    |   {hash:abc, size:4MB},  |                    |                  |
    |   {hash:def, size:4MB},  |                    |                  |
    |   {hash:ghi, size:4MB}]} |                    |                  |
    |                          |                    |                  |
    |                          |--check which chunks exist?---------->|
    |                          |<--"abc exists, def missing, ghi exists"
    |                          |                    |                  |
    |<--"upload only: [def]"---|                    |                  |
    |                          |                    |                  |
    |--upload chunk "def" ---->|--store chunk ------>|--PUT to S3 --->|
    |                          |                    |                  |
    |<--"upload complete" -----|                    |                  |
    |                          |                    |                  |
    |                          |--update metadata-->|                  |
    |                          |  (file → chunks mapping)             |
    |                          |                    |                  |
```

---

## 7. Metadata Database

### 7.1 File Tree Structure

```sql
-- File/Folder metadata (the "namespace")
CREATE TABLE file_metadata (
    file_id         BIGINT       PRIMARY KEY,
    user_id         BIGINT       NOT NULL,
    parent_id       BIGINT,                   -- NULL for root folder
    name            VARCHAR(255) NOT NULL,
    is_folder       BOOLEAN      DEFAULT FALSE,
    size_bytes      BIGINT       DEFAULT 0,
    mime_type       VARCHAR(100),
    latest_version  INT          DEFAULT 1,
    checksum        VARCHAR(64),              -- SHA-256 of full file
    status          ENUM('active', 'deleted', 'trashed'),
    created_at      TIMESTAMP,
    modified_at     TIMESTAMP,
    UNIQUE (user_id, parent_id, name),        -- no duplicate names in same folder
    INDEX idx_parent (parent_id),
    INDEX idx_user (user_id, status)
);

-- File versions
CREATE TABLE file_versions (
    file_id         BIGINT       NOT NULL,
    version_num     INT          NOT NULL,
    size_bytes      BIGINT,
    checksum        VARCHAR(64),
    device_id       VARCHAR(64),              -- which device made this version
    created_at      TIMESTAMP,
    PRIMARY KEY (file_id, version_num)
);

-- Chunks per version
CREATE TABLE file_chunks (
    file_id         BIGINT       NOT NULL,
    version_num     INT          NOT NULL,
    chunk_index     INT          NOT NULL,    -- 0, 1, 2, ...
    chunk_hash      VARCHAR(64)  NOT NULL,    -- SHA-256 of chunk
    chunk_size      INT          NOT NULL,
    PRIMARY KEY (file_id, version_num, chunk_index),
    INDEX idx_chunk_hash (chunk_hash)
);

-- Block storage reference counting
CREATE TABLE block_refs (
    chunk_hash      VARCHAR(64)  PRIMARY KEY,
    ref_count       INT          DEFAULT 1,   -- number of file_chunks referencing this
    size_bytes      INT          NOT NULL,
    storage_path    VARCHAR(255),
    created_at      TIMESTAMP
);
```

### 7.2 Example: File Tree

```
User Alice's file tree:

  / (root, file_id=1)
  ├── Documents/ (file_id=2)
  │   ├── report.pdf (file_id=3, v2, chunks: [abc, def, ghi])
  │   └── notes.txt (file_id=4, v1, chunks: [jkl])
  ├── Photos/ (file_id=5)
  │   ├── vacation.jpg (file_id=6, v1, chunks: [mno, pqr])
  │   └── profile.png (file_id=7, v1, chunks: [stu])
  └── shared_doc.docx (file_id=8, v5, chunks: [vwx, abc])
                                                     ^^^
                                    Same chunk "abc" as report.pdf!
                                    Stored only ONCE in S3.

  file_metadata table:
  +----+------+--------+-----------+-------+--------+
  | id | user | parent | name      | folder | ver   |
  +----+------+--------+-----------+--------+-------+
  | 1  | 100  | NULL   | /         | true   | -     |
  | 2  | 100  | 1      | Documents | true   | -     |
  | 3  | 100  | 2      | report.pdf| false  | 2     |
  | 4  | 100  | 2      | notes.txt | false  | 1     |
  | 5  | 100  | 1      | Photos    | true   | -     |
  +----+------+--------+-----------+--------+-------+
```

---

## 8. Sync Service

### 8.1 How Sync Works

The sync service is the **brain** of the system. It ensures all devices stay consistent.

```
Two types of changes to detect:

1. LOCAL CHANGES (client detects):
   - Client monitors local filesystem (inotify on Linux, FSEvents on macOS,
     ReadDirectoryChangesW on Windows)
   - When a file changes: compute new chunk hashes, compare with known state
   - Upload changed chunks, update metadata on server

2. REMOTE CHANGES (server notifies client):
   - Another device uploaded a change
   - Server notifies all other devices of the user
   - Client downloads changed chunks, reconstructs file locally
```

### 8.2 Change Detection Flow

```
Local change detected on Device A:

  Device A            Sync Client         API Server           Metadata DB
     |                    |                    |                    |
     | (file modified)    |                    |                    |
     |--notify----------->|                    |                    |
     |                    |--compute chunks--->|                    |
     |                    |  new hashes        |                    |
     |                    |                    |                    |
     |                    |--compare with ---->|                    |
     |                    |  server version    |                    |
     |                    |<--diff: chunks     |                    |
     |                    |  [2,5] changed     |                    |
     |                    |                    |                    |
     |                    |--upload chunk 2 -->|                    |
     |                    |--upload chunk 5 -->|                    |
     |                    |                    |                    |
     |                    |--commit version -->|--update metadata-->|
     |                    |  {file_id: 3,      |  {new version,    |
     |                    |   new_ver: 3,      |   chunk list}     |
     |                    |   chunks: [...]}   |                    |
     |                    |                    |                    |
     |                    |<--confirmed--------|                    |
     |                    |                    |                    |
     |                    |                    |--notify other ---->|
     |                    |                    |   devices          |

Server notifying Device B:

  API Server           Notification Svc       Device B Sync Client
     |                       |                        |
     |--file changed-------->|                        |
     |  {file_id: 3,         |                        |
     |   new_ver: 3}         |--push notification---->|
     |                       |  (long poll / WS)      |
     |                       |                        |
     |<--get changed chunks--+------------------------|
     |--chunks [2,5] data--->|                        |
     |                       |                        |--reconstruct
     |                       |                        |  file locally
```

### 8.3 Server-to-Client Notification

```
How does the server notify clients of remote changes?

Option 1: Long Polling
  - Client sends request: "Any changes since sequence X?"
  - Server holds connection open until a change occurs (up to 60 seconds)
  - On change: server responds immediately
  - On timeout: client re-sends request
  - Simple, firewall-friendly

Option 2: WebSocket
  - Persistent bidirectional connection
  - Server pushes change events immediately
  - Lower latency than long polling
  - Requires WebSocket server fleet

Decision: Long polling for simplicity (Dropbox's approach)
  - Simple to implement and scale
  - Minimal infrastructure
  - Good enough for file sync (not chat-level real-time)

  Client                          Notification Service
    |                                    |
    |--GET /changes?cursor=seq_123 ----->|
    |                                    |
    |     (server holds connection       |
    |      until change occurs           |
    |      or 60 sec timeout)            |
    |                                    |
    |         ... time passes ...        |
    |                                    |
    |<--200 OK: {changes: [{file_id: 3, |
    |           action: "modified",      |
    |           new_ver: 3}],            |
    |           cursor: seq_456}---------|
    |                                    |
    |--GET /changes?cursor=seq_456 ----->|
    |     (immediately re-connect)       |
```

---

## 9. Conflict Resolution

### 9.1 When Conflicts Occur

```
Conflict: Two devices edit the same file simultaneously while offline
  or before sync completes.

  Timeline:
    T=0:  Device A and Device B both have report.pdf v2
    T=1:  Device A edits report.pdf (offline)
    T=2:  Device B edits report.pdf (offline)
    T=3:  Device A comes online, uploads v3
    T=4:  Device B comes online, tries to upload v3
          SERVER DETECTS CONFLICT: B's base is v2, but v3 already exists!
```

### 9.2 Resolution Strategies

```
Strategy 1: Last-Write-Wins (LWW)
  - Whichever device uploads last wins
  - Lost data from earlier edit
  - Simple but DANGEROUS for important files
  - Used for: simple settings files, non-critical data

Strategy 2: Create Conflict Copy (Dropbox's approach)
  - Keep BOTH versions
  - Rename the conflicting file:
    "report.pdf" (Device A's version wins as v3)
    "report (Device B's conflicted copy).pdf" (saved separately)
  - User manually resolves the conflict
  - SAFE: no data loss

Strategy 3: Operational Transform / CRDT (Google Docs approach)
  - Merge changes at the operation level
  - Works for text documents (not binary files)
  - Complex to implement
  - Beyond scope for general file storage

Decision: Create conflict copy (Strategy 2)
  - Safe: no data loss
  - Simple to implement
  - User resolves manually (acceptable UX)

  Conflict Detection:
    1. Device B uploads: "update file_id=3, base_version=2, new chunks=[...]"
    2. Server checks: current version of file_id=3 is v3 (not v2!)
    3. Conflict detected: base_version (2) != current_version (3)
    4. Server creates conflict copy:
       - Original: report.pdf → v3 (Device A's version)
       - Conflict: "report (conflicted copy 2024-01-15).pdf" → new file
    5. Notify Device B: "conflict detected, created conflict copy"
```

---

## 10. File Versioning

### 10.1 Version Storage Architecture

```
Key insight: Versions SHARE chunks that haven't changed.

File: report.pdf

  Version 1: [chunk_A, chunk_B, chunk_C, chunk_D]
  Version 2: [chunk_A, chunk_B, chunk_E, chunk_D]  (chunk_C changed to E)
  Version 3: [chunk_A, chunk_F, chunk_E, chunk_D]  (chunk_B changed to F)

Storage:
  chunk_A: stored once, referenced by v1, v2, v3
  chunk_B: stored once, referenced by v1, v2
  chunk_C: stored once, referenced by v1 only
  chunk_D: stored once, referenced by v1, v2, v3
  chunk_E: stored once, referenced by v2, v3
  chunk_F: stored once, referenced by v3 only

  Total unique chunks: 6 (instead of 12 if stored per-version)
  Storage savings: 50% (varies based on how much changes between versions)

  file_chunks table:
  +------+-----+-------+--------+
  | file | ver | index | hash   |
  +------+-----+-------+--------+
  | 3    | 1   | 0     | A      |
  | 3    | 1   | 1     | B      |
  | 3    | 1   | 2     | C      |
  | 3    | 1   | 3     | D      |
  | 3    | 2   | 0     | A      |  ← shared
  | 3    | 2   | 1     | B      |  ← shared
  | 3    | 2   | 2     | E      |  ← new
  | 3    | 2   | 3     | D      |  ← shared
  | 3    | 3   | 0     | A      |  ← shared
  | 3    | 3   | 1     | F      |  ← new
  | 3    | 3   | 2     | E      |  ← shared
  | 3    | 3   | 3     | D      |  ← shared
  +------+-----+-------+--------+
```

### 10.2 Version Restore

```
To restore file to Version 1:
  1. Look up chunks for file_id=3, version=1: [A, B, C, D]
  2. All chunks still exist in block storage (reference counted)
  3. Create new version (v4) with same chunk list as v1
  4. No data copy needed! Just metadata update.

Version limits:
  - Keep last 30 versions per file
  - Or last 30 days of versions
  - After limit: garbage collect old versions
  - Chunks with ref_count = 0 can be deleted from S3
```

---

## 11. Deduplication

### 11.1 Content-Based Deduplication

```
How dedup works:

  1. Client computes SHA-256 of each chunk before upload
  2. Client sends chunk hashes to server: "I want to upload these chunks"
  3. Server checks which hashes already exist in block_refs table
  4. Server responds: "Only upload chunks [X, Y] — I already have [A, B, C]"
  5. Client uploads only the new chunks

  Result: If two users upload the same file (or same chunks appear
  in different files), the data is stored ONCE.

  Dedup Example:
    User Alice uploads presentation.pptx (chunks: [A, B, C, D])
    All chunks uploaded (new file)

    User Bob uploads the same presentation.pptx
    Server: "I already have [A, B, C, D], upload nothing!"
    Bob's file created instantly with zero data transfer!

  Cross-User Dedup:
    Same chunk stored once regardless of how many users have it
    block_refs.ref_count tracks how many file_chunks reference it
    Only delete when ref_count = 0

  Storage Savings:
    Typical enterprise: 30-50% dedup ratio
    (shared documents, email attachments, standard templates)
```

### 11.2 Security Consideration: Convergent Encryption

```
Problem: If we encrypt chunks with user-specific keys, 
         same content → different ciphertext → no dedup!

Solution: Convergent encryption
  - Encryption key = Hash(plaintext)
  - Same plaintext always produces same key and ciphertext
  - Enables dedup across users even with encryption
  
  key = SHA256(chunk_data)
  encrypted_chunk = AES-256(key, chunk_data)
  storage_hash = SHA256(encrypted_chunk)
  
  Tradeoff: Vulnerable to "confirmation of a file" attack
    (attacker who knows content can verify if it's stored)
  Acceptable risk for most enterprise use cases
```

---

## 12. Sharing and Permissions

### 12.1 ACL-Based Sharing

```sql
CREATE TABLE sharing_permissions (
    file_id         BIGINT       NOT NULL,
    shared_with     BIGINT       NOT NULL,   -- user_id or group_id
    shared_type     ENUM('user', 'group'),
    permission      ENUM('viewer', 'editor', 'owner'),
    shared_by       BIGINT       NOT NULL,   -- who shared it
    created_at      TIMESTAMP,
    PRIMARY KEY (file_id, shared_with, shared_type)
);

-- Public link sharing
CREATE TABLE public_links (
    link_token      VARCHAR(64)  PRIMARY KEY,
    file_id         BIGINT       NOT NULL,
    permission      ENUM('viewer', 'editor'),
    password_hash   VARCHAR(64),             -- optional password protection
    expires_at      TIMESTAMP,               -- optional expiration
    download_count  INT          DEFAULT 0,
    max_downloads   INT,                     -- optional download limit
    created_by      BIGINT       NOT NULL,
    created_at      TIMESTAMP
);
```

### 12.2 Permission Check Flow

```
When User B tries to access File X:

  1. Is User B the owner of File X? → Full access
  2. Check sharing_permissions:
     SELECT permission FROM sharing_permissions
     WHERE file_id = X AND shared_with = B AND shared_type = 'user';
  3. Check group memberships:
     SELECT permission FROM sharing_permissions sp
     JOIN group_members gm ON sp.shared_with = gm.group_id
     WHERE sp.file_id = X AND gm.user_id = B AND sp.shared_type = 'group';
  4. Check parent folder permissions (inheritance):
     Walk up the folder tree, check permissions at each level
  5. Most permissive wins (editor > viewer)

  Cache permissions in Redis: "perms:{file_id}:{user_id}" → "editor"
  TTL: 5 minutes (invalidate on permission change)
```

### 12.3 Public Link Flow

```
  Sharer creates link:
    POST /api/v1/files/{file_id}/share/link
    Response: { "url": "https://drive.example.com/s/aB3xK9zQm" }

  Recipient accesses link:
    GET /s/aB3xK9zQm
    1. Lookup link_token "aB3xK9zQm" in public_links table
    2. Check: expired? password-protected? download limit reached?
    3. Serve file viewer or download
```

---

## 13. Notification Service

### 13.1 Notifying Other Devices of Changes

```
When a file is changed, ALL other devices of the user (and shared users)
must be notified:

  File changed by User A on Device 1
       |
  +----v---------+
  | Sync Service |
  +----+---------+
       |
  +----v---------+
  | Notification |
  | Service      |
  +----+---------+
       |
       +---> User A, Device 2 (long poll response / WebSocket push)
       +---> User A, Device 3 (long poll response / WebSocket push)
       +---> User B (shared collaborator, via push notification)
       +---> User C (shared collaborator, via push notification)

  Notification payload:
    {
      "type": "file_changed",
      "file_id": 3,
      "action": "modified",    // created, modified, deleted, renamed, moved
      "new_version": 3,
      "changed_by": "user_A",
      "timestamp": 1705312200
    }
```

### 13.2 Change Feed (Sequence-Based)

```
Every change is assigned a monotonically increasing sequence number:

  Sequence  | Action      | File        | By
  --------- | ----------- | ----------- | --------
  seq_100   | created     | notes.txt   | Device A
  seq_101   | modified    | report.pdf  | Device A
  seq_102   | deleted     | old.doc     | Device B
  seq_103   | renamed     | notes.txt → memo.txt | Device A

Client tracks its cursor (last processed sequence):
  "Give me all changes after seq_100"
  → Returns seq_101, seq_102, seq_103

This enables:
  - Efficient sync: client only fetches what's new
  - Resumable: if client disconnects, resume from last cursor
  - Ordering: changes applied in correct order
```

---

## 14. Cold Storage for Old Versions

```
Not all versions need hot storage. Tiering strategy:

  Hot (S3 Standard):
    - Current version of all files
    - Last 5 versions (frequently restored)
    - Files accessed in last 30 days

  Warm (S3 Infrequent Access):
    - Versions 6-15
    - Files not accessed in 30-90 days
    - Lower cost, retrieval charge

  Cold (S3 Glacier):
    - Versions 16-30
    - Files not accessed in 90+ days
    - Very low cost, minutes-to-hours retrieval

  Transition rules (S3 Lifecycle):
    After 30 days no access  → move to S3-IA
    After 90 days no access  → move to Glacier
    After 365 days           → delete (or Glacier Deep Archive)

  Orphan Chunk Cleanup:
    - Periodic job: find chunks where ref_count = 0
    - Delete from S3 (no file references this chunk anymore)
    - Run weekly during off-peak
```

---

## 15. Encryption

### 15.1 Encryption Layers

```
1. In Transit:
   - All API calls over TLS 1.3
   - Chunk uploads/downloads over HTTPS
   
2. At Rest (Server-Side):
   - S3 Server-Side Encryption (SSE-S3 or SSE-KMS)
   - Each chunk encrypted with AES-256
   - Keys managed by AWS KMS
   - Transparent to the application

3. Client-Side Encryption (Optional, Premium Feature):
   - Client encrypts chunks BEFORE upload
   - Server never sees plaintext
   - User manages their own encryption key
   - Prevents even the service provider from reading files
   
   Flow:
     Client → Encrypt(chunk, user_key) → Upload encrypted chunk → S3
     Client ← Decrypt(chunk, user_key) ← Download encrypted chunk ← S3
   
   Tradeoff: Loses server-side search, preview, thumbnail generation
```

---

## 16. API Design

### 16.1 Core APIs

```
Upload File:
  POST /api/v1/files/upload/init
  Body: { "path": "/Documents/report.pdf", "chunks": [
    {"hash": "abc123", "size": 4194304},
    {"hash": "def456", "size": 4194304},
    {"hash": "ghi789", "size": 2097152}
  ]}
  Response: { "file_id": 12345, "upload_id": "up_xyz",
              "chunks_needed": ["def456"],  // only this one is new
              "upload_urls": {"def456": "https://s3.../presigned"} }

  PUT {presigned_url}  (upload chunk data directly to S3)

  POST /api/v1/files/upload/complete
  Body: { "upload_id": "up_xyz" }
  Response: { "file_id": 12345, "version": 3 }

Download File:
  GET /api/v1/files/{file_id}/download?version=3
  Response: { "chunks": [
    {"hash": "abc123", "url": "https://cdn.../abc123"},
    {"hash": "def456", "url": "https://cdn.../def456"},
    {"hash": "ghi789", "url": "https://cdn.../ghi789"}
  ]}

List Changes (Sync):
  GET /api/v1/changes?cursor=seq_100
  Response: { "changes": [...], "cursor": "seq_103", "has_more": false }

Share File:
  POST /api/v1/files/{file_id}/share
  Body: { "user_id": 456, "permission": "editor" }

Get Versions:
  GET /api/v1/files/{file_id}/versions
  Response: { "versions": [
    {"version": 3, "size": 10485760, "modified_at": "...", "device": "MacBook"},
    {"version": 2, "size": 10400000, "modified_at": "...", "device": "iPhone"},
    {"version": 1, "size": 10000000, "modified_at": "...", "device": "MacBook"}
  ]}
```

---

## 17. Full System Architecture

```
+------------------------------------------------------------------------+
|           CLIENTS (Desktop App / Mobile App / Web Browser)              |
|                                                                         |
|  +-------------------+  +------------------+  +-------------------+    |
|  | File System       |  | Chunking Engine  |  | Local Database    |    |
|  | Watcher           |  | (split, hash,    |  | (file metadata,  |    |
|  | (inotify/FSEvents)|  |  delta compute)  |  |  sync cursor)    |    |
|  +-------------------+  +------------------+  +-------------------+    |
+-----------------------------------+------------------------------------+
                                    |
                            +-------v--------+
                            | Load Balancer  |
                            +-------+--------+
                                    |
              +---------------------+---------------------+
              |                     |                     |
      +-------v-------+    +-------v-------+    +-------v-------+
      | API Server    |    | API Server    |    | API Server    |
      | (Stateless)   |    | (Stateless)   |    | (Stateless)   |
      +---+---+---+---+    +---------------+    +---------------+
          |   |   |
    +-----+   |   +------+
    |         |          |
+---v----+ +--v------+ +-v-----------+
| Sync   | | Block   | | Sharing &   |
| Service| | Service | | Permission  |
|(change | | (chunk  | | Service     |
| detect,| | upload/ | |             |
| notify)| | dedup)  | |             |
+---+----+ +----+----+ +----+--------+
    |           |            |
    |      +----v-------+    |
    |      | S3 / Object|    |
    |      | Storage    |    |
    |      | (chunks)   |    |
    |      +------------+    |
    |                        |
+---v------------------------v---+
|        Metadata Database       |
|        (PostgreSQL, sharded)   |
|  - file_metadata               |
|  - file_versions               |
|  - file_chunks                 |
|  - sharing_permissions         |
|  - block_refs                  |
+----------------+---------------+
                 |
    +------------v-----------+
    | Notification Service   |
    | (Long Polling /        |
    |  WebSocket gateway)    |
    +-----------+------------+
                |
    +-----------v------------+
    | Message Queue (Kafka)  |
    | - file change events   |
    | - sync notifications   |
    +------------------------+

    +------------------------+     +------------------------+
    | Garbage Collection     |     | Cold Storage Tiering   |
    | Service                |     | (S3 Lifecycle rules)   |
    | (orphan chunk cleanup) |     |                        |
    +------------------------+     +------------------------+
```

---

## 18. Key Trade-offs and Decisions

| Decision | Option A | Option B | Our Choice | Why |
|----------|----------|----------|------------|-----|
| Chunking | Fixed-size (4MB) | Content-defined (Rabin) | **Content-defined** | Better delta sync |
| Change notification | WebSocket | Long polling | **Long polling** | Simpler, sufficient for file sync |
| Conflict resolution | Last-write-wins | Conflict copy | **Conflict copy** | No data loss |
| Dedup scope | Per-user only | Cross-user | **Cross-user** | Max storage savings |
| Metadata DB | NoSQL | PostgreSQL | **PostgreSQL** | ACID, complex queries (tree) |
| Block storage | Self-hosted | S3 | **S3** | 11 nines durability, managed |
| Client-side encryption | Always on | Optional | **Optional** | Tradeoff with features |
| Version storage | Full copies | Shared chunks | **Shared chunks** | Massive storage savings |

---

## 19. Key Takeaways

```
+------------------------------------------------------------------+
| KEY TAKEAWAYS                                                     |
+------------------------------------------------------------------+
|                                                                    |
| 1. CHUNKING is the foundation — split files into 4MB chunks      |
|    for resumable upload, dedup, delta sync, and versioning        |
|                                                                    |
| 2. Content-addressable storage (SHA-256 hash as key) enables     |
|    automatic deduplication across users and versions              |
|                                                                    |
| 3. Delta sync is critical — only upload/download CHANGED chunks  |
|    (saves 50-80% bandwidth for edited files)                     |
|                                                                    |
| 4. File versions SHARE unchanged chunks — versioning costs       |
|    proportional to what changed, not total file size              |
|                                                                    |
| 5. Conflict resolution via "conflict copy" is safest —           |
|    never lose user data, let user resolve manually                |
|                                                                    |
| 6. Long polling is sufficient for file sync notifications —      |
|    5-second sync latency is acceptable for file storage           |
|                                                                    |
| 7. Metadata DB (PostgreSQL) stores the file tree and chunk       |
|    mappings; Block store (S3) stores actual chunk data            |
|                                                                    |
| 8. Client-side file watcher detects local changes instantly —    |
|    OS-level APIs (inotify, FSEvents) for real-time detection     |
|                                                                    |
| 9. Cold storage tiering (S3 → S3-IA → Glacier) optimizes        |
|    cost for old versions and inactive files                       |
|                                                                    |
| 10. Security: TLS in transit, AES-256 at rest, optional          |
|     client-side encryption for zero-knowledge storage             |
|                                                                    |
+------------------------------------------------------------------+
```

---

*Previous: [Chapter 32 - Notification System](./32-notification-system.md) | Next: [Chapter 34 - Typeahead / Autocomplete](./34-typeahead.md)*
