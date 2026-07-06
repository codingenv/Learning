# Understanding Large Language Models

### A Beginner's Journey from Zero to Confident Understanding

---

> **Who this book is for:** Anyone curious about how tools like ChatGPT, Claude, and Gemini actually work. No prior knowledge of AI, math, or programming is required.
>
> **What you will be able to do after reading:** Confidently explain what an LLM is, how it learns, how it turns your words into answers, what it is good at, where it fails, and how to use it well.
>
> **How to read this book:** Each chapter builds on the previous one. Read them in order. Every chapter ends with a quick summary, a few reflection questions, and a small exercise. Take your time -- understanding beats speed.

---

## Table of Contents

1. [Introduction to AI and LLMs](#chapter-1-introduction-to-ai-and-llms)
2. [Fundamentals of Language Models](#chapter-2-fundamentals-of-language-models)
3. [Understanding Tokens](#chapter-3-understanding-tokens)
4. [Core Concepts Behind LLMs](#chapter-4-core-concepts-behind-llms)
5. [Transformer Architecture](#chapter-5-transformer-architecture)
6. [Types of LLM Models](#chapter-6-types-of-llm-models)
7. [Popular and Frequently Used LLMs](#chapter-7-popular-and-frequently-used-llms)
8. [How LLMs Are Trained](#chapter-8-how-llms-are-trained)
9. [How LLMs Generate Text](#chapter-9-how-llms-generate-text)
10. [Strengths and Limitations of LLMs](#chapter-10-strengths-and-limitations-of-llms)
11. [Practical Usage](#chapter-11-practical-usage)
12. [Future of LLMs](#chapter-12-future-of-llms)
13. [Glossary of Key Terms](#glossary-of-key-terms)

---

## Chapter 1: Introduction to AI and LLMs

Welcome. Before we talk about Large Language Models, let us build the ground floor. LLMs sit at the top of a tall building, and the floors below them are AI, Machine Learning, and Deep Learning. Let us climb up one floor at a time.

### 1.1 What is Artificial Intelligence?

**Artificial Intelligence (AI)** is the broad idea of making machines do things that normally require human intelligence -- like understanding language, recognising faces, making decisions, or playing chess.

Think of AI as a giant umbrella term. Anything where a computer appears to "think" or "act smart" falls under this umbrella.

**Analogy:** Imagine the word "vehicle." It covers bicycles, cars, trucks, planes, and boats. AI is like the word "vehicle" -- a huge category. Inside it are smaller, more specific categories.

**Everyday examples of AI:**
- A navigation app finding the fastest route home
- An email app filtering spam into a separate folder
- A streaming service suggesting your next movie
- A chess program that beats world champions

Here is a key point that surprises beginners: **AI does not mean the machine is conscious or alive.** It simply means the machine follows clever rules or patterns to produce useful, "intelligent-looking" results.

### 1.2 What is Machine Learning?

Early AI was built by humans writing thousands of exact rules: "IF the email contains the word 'lottery', THEN mark it as spam." This worked, but it was slow, fragile, and could not handle surprises.

**Machine Learning (ML)** is a smarter approach. Instead of a human writing every rule, we **show the computer lots of examples and let it figure out the rules itself.**

**Analogy:** Imagine teaching a child what a "cat" is. You do not hand them a rulebook saying "a cat has pointy ears, whiskers, and fur." Instead, you point at many cats and say "cat, cat, cat." After enough examples, the child just *knows* a cat when they see one -- even a kind they have never seen before. Machine Learning works the same way: learning from examples rather than from explicit rules.

```
RULE-BASED (old way):
   Human writes rules  ->  Computer follows rules  ->  Output

MACHINE LEARNING (new way):
   Human gives examples  ->  Computer finds patterns  ->  Computer makes its own rules  ->  Output
```

**Real-world ML examples:**
- A bank predicting whether a loan will be repaid, based on thousands of past loans
- A photo app recognising your friends' faces after seeing tagged photos
- A weather model predicting tomorrow's temperature from years of past data

ML is a *part* of AI. So on our building, ML is one floor inside the AI umbrella.

### 1.3 What is Deep Learning?

**Deep Learning (DL)** is a powerful *type* of Machine Learning inspired by how the human brain works. It uses something called a **neural network** -- a web of tiny connected units (called "neurons") arranged in layers.

The word "deep" simply means there are **many layers** stacked on top of each other. Each layer learns to recognise something slightly more complex than the layer before it.

**Analogy:** Imagine recognising a human face, layer by layer:
- **Layer 1** notices simple edges and lines.
- **Layer 2** combines edges into shapes like circles and curves.
- **Layer 3** combines shapes into parts like eyes, a nose, a mouth.
- **Layer 4** combines parts into a whole face.

Each layer passes its findings to the next, building understanding step by step -- just like an assembly line where each station adds something.

```
A NEURAL NETWORK (simplified)

  Input        Hidden Layers (the "deep" part)        Output
            
  [pixel] --\                                      
  [pixel] ---\--> (o) --> (o) --> (o) --\          
  [pixel] ----\-> (o) --> (o) --> (o) ---\-->  "This is a cat"
  [pixel] ----/-> (o) --> (o) --> (o) ---/          
  [pixel] ---/--> (o) --> (o) --> (o) --/           
  [pixel] --/                                       

  (o) = a tiny "neuron" that does a small calculation
```

Deep Learning is what made modern AI explode in ability. It powers voice assistants, self-driving car vision, and -- importantly for us -- **Large Language Models.**

### 1.4 Where Do LLMs Fit In?

Now we reach the top floor. Let us put the whole building together:

```
+--------------------------------------------------------------+
|  ARTIFICIAL INTELLIGENCE (the big umbrella)                  |
|                                                              |
|   +------------------------------------------------------+   |
|   |  MACHINE LEARNING (learning from examples)           |   |
|   |                                                      |   |
|   |   +----------------------------------------------+   |   |
|   |   |  DEEP LEARNING (neural networks, many layers)|   |   |
|   |   |                                              |   |   |
|   |   |     +------------------------------------+   |   |   |
|   |   |     |  LARGE LANGUAGE MODELS (LLMs)      |   |   |   |
|   |   |     |  Deep learning specialised for     |   |   |   |
|   |   |     |  understanding & generating text   |   |   |   |
|   |   |     +------------------------------------+   |   |   |
|   |   +----------------------------------------------+   |   |
|   +------------------------------------------------------+   |
+--------------------------------------------------------------+
```

A **Large Language Model (LLM)** is a deep learning system trained on enormous amounts of text -- books, websites, articles, conversations -- so that it learns the patterns of human language. Once trained, it can read your question and produce a helpful, human-like response.

**Why "Large"?** Because these models are genuinely huge in two ways:
1. **Huge training data:** trillions of words, roughly the size of millions of books.
2. **Huge number of internal settings (called "parameters"):** modern LLMs have billions or even trillions of these adjustable knobs.

**The simplest possible definition:** An LLM is a very advanced "next-word predictor" that has read a large portion of the internet and learned to continue text in a sensible, useful way. (We will unpack exactly how in later chapters -- this simple idea is surprisingly powerful.)

### 1.5 Real-World Applications of LLMs

You have almost certainly used or benefited from an LLM already. Here are common real-world uses:

- **Chat assistants:** ChatGPT, Claude, and Gemini answer questions and hold conversations.
- **Writing help:** drafting emails, fixing grammar, summarising long documents.
- **Coding help:** explaining code, finding bugs, writing small programs.
- **Customer support:** chatbots that answer common questions instantly.
- **Translation:** converting text between languages.
- **Search and research:** summarising search results into a direct answer.
- **Education:** explaining hard topics in simple words (like this book could have been written with help from one).

**Real example walk-through:** You type, "Explain photosynthesis to a 10-year-old." The LLM reads your words, understands you want a *simple* explanation for a *child*, and generates a friendly paragraph using easy language and an analogy -- because it has seen millions of examples of simple explanations during training.

### Chapter 1 Summary

- **AI** is the broad goal of making machines act intelligently.
- **Machine Learning** is a way to achieve AI by learning from examples instead of hand-written rules.
- **Deep Learning** is a powerful kind of ML using layered neural networks.
- **LLMs** are deep learning systems specialised for understanding and generating human language.
- At its core, an LLM is an extremely capable **next-word predictor** trained on massive text.

### Reflection Questions

1. In your own words, how is Machine Learning different from a human writing fixed rules?
2. Why do you think the word "deep" is used in Deep Learning?
3. Can you name three apps or services you used this week that probably use AI?

### Exercise

Draw the four nested boxes (AI > ML > DL > LLM) from memory, and write one sentence describing each. If you can do this, you have the mental map for the whole book.

---

## Chapter 2: Fundamentals of Language Models

In Chapter 1 we kept repeating a single, almost suspiciously simple phrase: an LLM is a "next-word predictor." That phrase is the seed from which the entire field of Large Language Models grows. In this chapter we are going to plant that seed and watch it sprout.

This is a long, foundational chapter, and that is deliberate. If you truly understand what a "language model" is and how the idea evolved over decades, then everything else in this book -- tokens, embeddings, attention, transformers, training, generation -- will feel like natural extensions of one core idea, rather than a pile of disconnected jargon.

So we will go slowly. We will use lots of analogies, plenty of examples, and we will not assume any prior math or programming knowledge. By the end, you will be able to explain to a friend, with confidence: *what a language model is, how it assigns probabilities, why early models were limited, how each generation improved, and why "just predicting the next word" turned out to be one of the most powerful ideas in modern technology.*

Let us begin.

### 2.1 What is a Language Model? (The Core Idea)

A **language model** is, at heart, a system that answers one question over and over again:

> "Given the words I have seen so far, what word is likely to come next?"

That is it. That single repeated question is the engine behind every chatbot, every autocomplete, every AI writing assistant you have ever used.

Let us make this concrete. Suppose you read the beginning of a sentence:

```
"I poured myself a cup of hot ___"
```

Your brain instantly produces candidates: *coffee, tea, chocolate, water.* It almost certainly does **not** suggest *bicycle, Tuesday,* or *purple.* Why not? Because in all the language you have heard and read in your life, "a cup of hot" is followed by drinks, not vehicles or colours. You have, without ever being taught a rule, learned the *patterns* of language.

A language model does exactly the same thing -- it has "read" enormous amounts of text and learned which words tend to follow which other words. When you give it some text, it predicts what comes next based on those learned patterns.

**A friendly analogy: the autocomplete on your phone.** When you type "I am running a little...", your keyboard suggests "late." It does not suggest "elephant." Your phone's keyboard is a small, simple language model. It has seen many messages, noticed that "running a little" is usually followed by "late," and offers that prediction. A Large Language Model is the same idea scaled up dramatically -- vastly more text, vastly more patterns, vastly better predictions.

**The key shift in thinking:** A language model does not "look up" answers in a stored list of facts like a dictionary or database. Instead, it has absorbed the *statistical patterns* of language so deeply that the next word simply "falls out" as the most fitting continuation. This is closer to how a fluent speaker finishes your sentence than to how a search engine retrieves a document. Hold on to this distinction -- it explains both the magic and the limitations of LLMs that we will explore in later chapters.

### 2.2 The Idea of "Probability" Made Simple

You will often hear that a language model "assigns probabilities" to words. This sounds mathematical and intimidating, but the idea is genuinely simple. Let us unpack it gently, because it is the beating heart of how these models work.

A **probability** is just a number that says *how likely* something is. We can express it as a percentage from 0% (impossible) to 100% (certain), or as a decimal from 0 to 1. For example:
- The probability of a coin landing heads is 50% (0.5).
- The probability that the sun rises tomorrow is essentially 100% (1.0).
- The probability of rolling a 7 on a single six-sided die is 0% (it is impossible).

Now here is the crucial point. When a language model looks at "The sky is ___", it does **not** pick a single answer with total certainty. Instead, it spreads its confidence across *many* possible next words, giving each one a probability:

```
PREDICTING THE NEXT WORD AFTER "The sky is"

  Candidate word     Probability (how likely)
  ---------------    ------------------------
  "blue"             ##############   45%
  "clear"            #######          20%
  "falling"          ###              10%
  "grey"             ###              10%
  "beautiful"        ##                7%
  ... thousands more, each tiny ...     8%
  "sandwich"         (almost zero)    ~0%
```

This collection of words-with-their-probabilities is called a **probability distribution.** Think of it as the model spreading 100 points of confidence across all the words it knows, giving more points to words that fit well and almost none to words that do not.

**An everyday analogy: a weather forecast.** A meteorologist does not say "it will definitely rain." They say "there is a 70% chance of rain, a 20% chance of clouds, and a 10% chance of sun." They are spreading their confidence across possible outcomes based on patterns they have observed. A language model forecasts the *next word* in exactly the same spirit -- a "weather forecast for words."

**Why probabilities instead of one fixed answer?** Because language is genuinely flexible. After "I love you so ___", many words are reasonable: "much," "deeply," "very." There is no single "correct" word. By working in probabilities, the model captures this natural flexibility -- and, as we will see in Chapter 9, this is also what lets a model be *creative* or *consistent* depending on how we ask it to choose from the distribution.

### 2.3 How Does a Language Model Learn These Probabilities?

We have said the model "learned patterns" from lots of text. But what does "learning" actually mean here? We will cover full training in Chapter 8, but let us build the intuition now, because it makes everything clearer.

Imagine you wanted to learn, by hand, what word follows "peanut butter and." You could gather a huge pile of books and articles and simply **count**:

```
Times "peanut butter and ___" appears in your text:
  "jelly"    -> appeared 850 times
  "honey"    -> appeared 120 times
  "banana"   -> appeared  90 times
  "chocolate"-> appeared  40 times
  "sadness"  -> appeared   0 times
```

From these counts, you can estimate probabilities: "jelly" is by far the most likely, "honey" and "banana" are possible, and "sadness" never happens. Congratulations -- you have just built the simplest kind of language model by counting. This counting approach is exactly how the earliest models worked, and we will meet them properly in the next section.

Modern neural language models do something far more sophisticated than counting, but the *goal* is identical: figure out how likely each possible next word is. The difference is that instead of storing literal counts, they adjust billions of internal "knobs" (parameters) so that, when shown some text, they output a good probability distribution for the next word. They learn not just exact phrases but deep, flexible patterns -- grammar, meaning, style, and even facts.

**Analogy: learning to cook by taste vs by recipe card.** Counting is like keeping a giant card file: "if the dish has tomatoes, add basil 80% of the time." A neural model is more like a chef who has cooked thousands of meals until they simply *know* what works together -- they can handle new ingredients they have never seen in a specific combination, because they have internalised the underlying patterns of flavour. That flexibility is why neural models eventually crushed the old counting methods.

### 2.4 The Evolution of Language Models: A Journey Through Time

Language models did not arrive fully formed. They evolved over many decades, with each generation fixing the weaknesses of the one before. Understanding this journey is the best way to appreciate *why* modern models are built the way they are. Let us walk through the major eras, like visiting the ancestors in a family tree.

```
THE FAMILY TREE OF LANGUAGE MODELS

  Rule-based     N-grams        Neural nets     RNNs/LSTMs     Transformers
  (hand-written) (counting)     (early learning)(memory)       (attention)
   ~1950s-80s     ~1980s-2000s   ~2000s          ~2010s         2017-now
      |              |              |               |               |
   rigid rules   short memory   first real      sequential      look at
   by humans     but simple     "learning"      memory, but     everything
                                of patterns     fades on long   at once;
                                                sentences       powers all
                                                                modern LLMs
```

#### Stage 0: Rule-Based Systems (the prehistoric era)

Before statistics and learning, the earliest attempts at handling language used **hand-written rules** crafted by linguists and programmers. A human would write something like: "A sentence is a noun phrase followed by a verb phrase," and list grammar rules by hand.

**Why it failed:** Human language is gloriously messy. It is full of exceptions, slang, idioms, sarcasm, and constant change. Writing enough rules to cover real language proved impossible -- there were always more exceptions than rules. It was like trying to write down every possible chess game move by move. This frustration is exactly what pushed researchers toward letting machines *learn* patterns from data instead.

#### Stage 1: N-Gram Models (the counting era)

This is the first real "language model" in the statistical sense, and it is worth understanding deeply because it makes the later breakthroughs shine by contrast.

**What is an n-gram?** An "n-gram" is simply a group of *n* words in a row. The "n" is just a number:
- A **unigram** is 1 word: "cat"
- A **bigram** is 2 words: "the cat"
- A **trigram** is 3 words: "the cat sat"
- A **4-gram** is 4 words: "the cat sat on"

**How an n-gram model works.** The model predicts the next word by looking only at the previous *n-1* words and asking, "Based on my counts, what usually comes next?" For example, a **trigram** model (which looks at the previous 2 words) predicting the word after "the cat":

```
TRIGRAM PREDICTION: what follows "the cat"?

  "the cat sat"     -> seen 1,200 times  (most likely)
  "the cat ran"     -> seen   600 times
  "the cat slept"   -> seen   300 times
  "the cat sang"    -> seen     2 times  (very unlikely)
```

The model picks "sat" because that combination was most common in its text. Simple, fast, and for its time, genuinely useful. N-gram models powered early autocomplete, spell-checkers, and even early speech recognition.

**Now, the three serious weaknesses of n-gram models:**

**Weakness 1 -- Goldfish memory.** This is the big one. An n-gram model only remembers the last *n-1* words and forgets everything before. Watch what goes wrong:

```
THE MEMORY PROBLEM

  "I grew up in France, so I speak fluent ___"
   \________________/           \___________/
     CRUCIAL CLUE                only THIS is
     (but forgotten!)            seen by a trigram

  A trigram model only sees "speak fluent" and might guess
  "English" -- because it has completely forgotten the word
  "France" that appeared earlier. The most important clue is lost.
```

Human readers instantly use the word "France" to predict "French." An n-gram model, with its tiny memory window, simply cannot connect clues that are far apart. This is a fatal flaw for understanding real language, where meaning often depends on words mentioned much earlier.

**Weakness 2 -- The explosion problem (and the sparsity that follows).** You might think, "Just use a bigger n! A 10-gram would remember 9 words!" But this creates a catastrophe of numbers. The possible combinations explode astronomically:

```
THE COMBINATORIAL EXPLOSION

  Vocabulary of just 50,000 words...
  Bigrams (2 words):   50,000 x 50,000        = 2.5 billion combos
  Trigrams (3 words):  50,000^3               = 125 trillion combos
  4-grams (4 words):   50,000^4               = an absurd number
```

There is not enough text in the world -- or memory in any computer -- to count and store all these combinations. And here is the cruel twist, called **sparsity**: most valid word combinations will *never appear* in your training text even once, simply because there are too many possibilities. So when the model meets a perfectly normal phrase it happened not to see during counting, it assigns it a probability of **zero** -- as if it were impossible. The model becomes brittle and surprised by ordinary language.

(Researchers invented clever patches called "smoothing" -- essentially sprinkling a tiny bit of probability onto unseen combinations so nothing is truly zero -- but these were band-aids on a fundamental flaw.)

**Weakness 3 -- No sense of meaning.** To an n-gram model, words are just distinct symbols with no relationships. It has no idea that "cat" and "kitten" are related, or that "big" and "large" mean nearly the same thing. If it learned a pattern about "car," none of that knowledge transfers to "automobile." Every word is an island. This means the model wastes its limited counting on near-duplicate patterns and cannot generalise. (This very weakness is what *embeddings* will brilliantly solve -- a preview of Chapter 4.)

**Analogy for the whole n-gram era:** An n-gram model is like trying to understand a movie by looking at only two frames at a time, with no memory of the earlier scenes and no understanding of who the characters are. You can guess the very next frame sometimes, but you can never follow the plot.

#### Stage 2: Early Neural Language Models (the dawn of learning)

In the early 2000s, researchers asked a powerful question: instead of *counting* word combinations, what if a system could **learn** patterns using a neural network (the brain-inspired layered system from Chapter 1)?

This brought two revolutionary improvements:

**Improvement 1 -- Words gained meaning (the birth of embeddings).** Neural models began representing each word as a list of numbers (a "vector") that captured its meaning. Words used in similar ways ended up with similar numbers. Suddenly "cat" and "kitten" were close together, and the model could *generalise* -- knowledge about one word could help with related words. (This is the embeddings idea, which we explore fully in Chapter 4. It is one of the most important concepts in the whole book.)

**Improvement 2 -- Graceful handling of the unseen.** Because the model learned smooth patterns rather than rigid counts, it no longer slammed unseen-but-reasonable phrases to zero probability. It could make sensible guesses about combinations it had never literally encountered, just as you can understand a brand-new sentence you have never read before.

These early neural models were a genuine leap forward. But they still mostly looked at a fixed, smallish window of recent words. The dream of a model that could remember and connect *distant* parts of a text was still out of reach. Enter the next stage.

#### Stage 3: Recurrent Neural Networks (RNNs) and LSTMs (the memory era)

The next big idea was to give the model a **memory that carries forward** as it reads. A **Recurrent Neural Network (RNN)** processes text one word at a time, and after each word it updates an internal "memory" (often called a hidden state) that summarises everything it has read so far.

```
HOW AN RNN READS (one word at a time, carrying memory)

  word1 --> [memory] --> word2 --> [memory] --> word3 --> [memory] --> predict
            updated               updated                updated
            after word1           after word2            after word3

  The memory is passed along like a baton in a relay race,
  hopefully carrying useful information from the start to the end.
```

This was a major step. In principle, an RNN could carry information from the beginning of a sentence all the way to the end -- something n-grams could never do. It read like a person reads: left to right, building up understanding.

**But RNNs had a stubborn problem: fading memory.** As the relay baton (the memory) was passed from word to word, the information from early words got weaker and weaker, eventually fading into noise. By the end of a long paragraph, the model had effectively forgotten how it began.

**Analogy: the game of telephone.** Remember the children's game where a message is whispered from person to person around a circle? By the time it reaches the last person, "I bought a green hat" has become "I fought a mean cat." RNNs suffered the same way -- information degraded as it travelled through many steps. (Engineers have a technical name for this, the "vanishing gradient problem," but the telephone analogy captures the spirit perfectly.)

**A clever upgrade: LSTMs and GRUs.** Researchers invented improved RNNs called **LSTMs** (Long Short-Term Memory networks) and **GRUs** (Gated Recurrent Units). These added little "gates" -- think of them as smart valves -- that let the model decide what to *keep* in memory, what to *forget*, and what to *pay attention to*. This dramatically improved how long the memory lasted, and LSTMs powered many useful systems through the 2010s, including early machine translation and voice assistants.

**But two fundamental limits remained:**
1. **Memory still faded** on very long texts -- the gates helped, but did not fully solve it.
2. **They were slow to train.** Because an RNN must read words strictly one after another (word 2 cannot be processed until word 1 is done), it could not take full advantage of modern hardware that loves doing many things *at the same time* (in parallel). Reading a long book one word at a time, in strict order, is simply slow.

The field needed something that could (a) connect any two words no matter how far apart, and (b) process words in parallel for speed. In 2017, that something arrived.

#### Stage 4: Transformers (the breakthrough that changed everything)

In 2017, a research paper with the bold title **"Attention Is All You Need"** introduced the **Transformer** architecture. It solved both of the RNN's fundamental problems in one elegant stroke, and it is the foundation of every modern LLM -- GPT, BERT, Claude, Gemini, LLaMA, all of them.

The Transformer's superpower is a mechanism called **attention** (the full focus of Chapters 4 and 5). For now, here is the intuition:

Instead of passing a fading memory baton from word to word, a Transformer lets **every word look directly at every other word, all at once,** and decide which ones are most relevant. Nothing has to travel through a long, lossy chain. The word "French" at the end of a sentence can look *directly* back at "France" at the beginning, no matter how far apart they are -- with no fading.

```
RNN vs TRANSFORMER (the key difference)

  RNN:          word1 -> word2 -> word3 -> word4   (one at a time,
                  \________baton of memory_______/   memory fades)

  TRANSFORMER:  word1   word2   word3   word4
                  \  \  /  \  /  /  \  /  /
                   every word connects directly to every other word,
                   all at the same time -- nothing fades, and it is fast
```

This gave Transformers two enormous advantages:
1. **Excellent long-range memory.** Distant words connect directly, so meaning is never lost over long passages.
2. **Speed through parallelism.** Because all words are processed together rather than one-by-one, Transformers train extremely fast on modern hardware -- which is exactly what made it possible to train on *trillions* of words and build truly Large Language Models.

We will not go deeper here -- Chapters 4 and 5 are devoted entirely to attention and Transformers. The takeaway for now: the Transformer is the engine, and "predicting the next word" is the fuel. Everything modern flows from this combination.

### 2.5 Two Flavours of Language Modeling (a gentle preview)

Before we leave the mechanics, it helps to know that "predicting words" comes in two main styles. You will see these again in Chapter 6, so just meet them lightly here.

**1. Predicting the *next* word (causal / autoregressive modeling).** The model only sees the words *before* the blank and predicts what comes next, left to right. This is how *writing* models like GPT work -- perfect for generating text one word at a time.

```
  "The weather today is very ___"   -> predict the next word
   (only looks backward)
```

**2. Filling in a *missing* word in the middle (masked modeling).** The model sees the words on *both sides* of a blank and predicts the hidden word. This is how *understanding* models like BERT work -- great for comprehension because they use full context.

```
  "The weather today is very [MASK] and sunny."  -> predict the masked word
   (looks both backward AND forward)
```

Both are "language modeling" -- both are about predicting words from context. They simply differ in *which* words are hidden. This single choice is the root of the difference between "writer" models and "understander" models that we will explore in Chapter 6. For now, just notice that "predict the word" is a flexible idea that can be set up in more than one way.

### 2.6 Why Is Language Modeling So Important? (The Deep Magic)

We now arrive at the most mind-bending idea in this chapter. You might still be thinking: *"Okay, the model predicts the next word. So what? Why has this created such a revolution?"*

Here is the profound insight:

> **To predict the next word *really* well, a model is forced to learn an enormous amount about language, facts, reasoning, and the world -- whether we intended it to or not.**

Let us prove this to ourselves. Look at what a model must "know" to correctly finish each of these sentences:

```
WHAT THE MODEL SECRETLY HAS TO LEARN

  "The capital of Japan is ___"
       -> requires FACTS about the world (answer: Tokyo)

  "She was exhausted after the marathon, so she ___"
       -> requires CAUSE-AND-EFFECT reasoning (e.g., "rested")

  "2 plus 2 equals ___"
       -> requires basic MATH (answer: 4)

  "The detective realised the butler was lying because ___"
       -> requires LOGIC and understanding of human behaviour

  "Roses are red, violets are ___"
       -> requires CULTURAL knowledge (answer: blue)

  "Translate to French: 'good morning' -> ___"
       -> requires knowledge of ANOTHER LANGUAGE (answer: bonjour)
```

Notice what is happening. We never explicitly taught the model geography, arithmetic, logic, poetry, or French. We only ever asked it to do one thing: **predict the next word.** But the only way to get *really good* at that single task across billions of diverse sentences is to *accidentally* absorb facts, grammar, reasoning, and culture along the way.

**This is the central magic of LLMs:** next-word prediction is merely the *training task*, but deep, broad understanding is the *side effect* -- and that side effect is what makes these models so astonishingly useful.

**A powerful way to see it -- prediction as compression.** Here is another lens that experts love. To predict text well, a model must find the underlying patterns and rules that generate that text -- in effect, *compressing* the messiness of human knowledge into its parameters. And it turns out that **understanding and compression are deeply related.** A model that can compress the patterns of all human writing into a set of internal knobs has, in a real sense, built a working model of how language and the world behave.

**Analogy: the student who studies to ace every test.** Imagine a student told only: "You must correctly fill in the blank on any sentence from any book, on any subject." To succeed, they cannot just memorise -- there are infinitely many sentences. They are *forced* to actually learn history, science, math, and reasoning, because that is the only way to predict well across everything. The "fill in the blank" task quietly turns them into a polymath. LLMs are that student, scaled to a superhuman amount of reading.

**Why "bigger" kept getting better (a note on scale).** Researchers discovered something remarkable: as they made models bigger (more parameters) and trained them on more text, the models did not just get a little better at predicting words -- they began to display new abilities that smaller models simply did not have, like basic reasoning, translation, and following instructions. These surprising new skills that "emerge" with scale are part of why the race to build ever-larger models took off. We will not dive into the details, but keep this in mind: the simple next-word task, applied at massive scale, produced capabilities that genuinely surprised even the researchers who built these systems.

### 2.7 Setting Honest Expectations: What This Does and Does Not Mean

Because this chapter celebrates how powerful next-word prediction is, it is only fair to plant an early seed of caution (we will harvest it fully in Chapter 10).

The fact that a model learns facts and reasoning as a *side effect* of prediction also explains its weaknesses:
- The model's goal is to produce text that is **likely**, not text that is guaranteed **true.** Usually likely and true overlap -- but not always. This is the root of "hallucinations" (confident-sounding mistakes).
- The model reflects the patterns in its training text, including human **biases** and errors.
- The model has no built-in mechanism for checking facts against reality; it is forecasting words, not consulting a verified database.

None of this diminishes how remarkable LLMs are. It simply means we should understand them for what they truly are: extraordinary pattern-predictors, not infallible oracles. Keeping this honest framing will make you a wiser and more effective user of these tools.

### 2.8 From One Prediction to a Whole Essay (a preview of generation)

One last question naturally arises: if the model only predicts *one* next word, how does it produce whole paragraphs, stories, and answers?

The answer is beautifully simple, and we will explore it fully in Chapter 9. The model predicts one word, adds it to the text, and then predicts the *next* word based on the now-slightly-longer text -- over and over, like a snowball rolling downhill and growing:

```
GROWING TEXT ONE WORD AT A TIME

  "The"               -> predicts "sun"
  "The sun"           -> predicts "rose"
  "The sun rose"      -> predicts "over"
  "The sun rose over" -> predicts "the"
  ... and so on, until a whole story emerges ...
```

So the entire impressive output of an LLM is just this one humble prediction, repeated again and again, each time feeding its own output back in. The "next-word predictor" really is the whole show -- it just runs in a loop. We will see exactly how the model chooses each word (and how we can make it more creative or more focused) in Chapter 9.

### Key Terms Recap

Before the summary, let us lock in the vocabulary introduced in this chapter:

- **Language model:** a system that predicts how likely each possible next word is, given the words so far.
- **Probability distribution:** the spread of confidence across all possible next words (like a weather forecast for words).
- **N-gram:** a group of n consecutive words; early models predicted by counting these.
- **Sparsity:** the problem that most valid word combinations never appear in the training text, so counting-based models break.
- **RNN (Recurrent Neural Network):** a model that reads word by word, carrying a memory forward (but the memory fades).
- **LSTM/GRU:** improved RNNs with "gates" that remember longer.
- **Transformer:** the 2017 breakthrough that lets every word attend to every other word at once -- the basis of all modern LLMs.
- **Attention:** the mechanism letting words focus directly on the most relevant other words (full detail in Chapters 4-5).
- **Causal (autoregressive) vs masked modeling:** predicting the *next* word (looking backward) vs filling in a *missing* word (looking both ways).

### Chapter 2 Summary

- A **language model** answers one repeated question: "given the words so far, what word probably comes next?"
- It works with **probabilities** -- spreading confidence across many possible words, like a weather forecast, rather than picking one certain answer.
- Models **learn** these probabilities from huge amounts of text; the earliest did it by literally **counting** word combinations.
- The evolution went through clear stages: **rule-based** (rigid, failed), **n-grams** (counting; tiny memory, explosion, and sparsity problems), **early neural models** (gave words meaning via embeddings), **RNNs/LSTMs** (carried memory forward, but it faded -- the "telephone game"), and finally **Transformers** (2017), which connect all words at once and run fast.
- Language modeling comes in two flavours: **causal** (predict the next word -- "writer" models like GPT) and **masked** (fill in a missing word -- "understander" models like BERT).
- The deep magic: to predict the next word *really well*, a model is **forced to learn facts, grammar, reasoning, and culture** as a side effect. Prediction is the task; understanding is the by-product.
- This same mechanism explains LLM **limitations**: they produce *likely* text, not guaranteed *true* text.
- Whole essays come from running the one-word prediction in a **loop**, feeding each output back in.

### Reflection Questions

1. In your own words, what single question does every language model repeatedly try to answer?
2. Why is it better for a model to output a *probability distribution* over many words rather than committing to one certain word? Can you think of a sentence where several different next words would all be reasonable?
3. Explain the n-gram "memory problem" using your own example sentence where an early clue is needed to predict a later word.
4. What were the two fundamental problems RNNs had, and how did the Transformer solve both?
5. The chapter claims that "predicting the next word" forces a model to learn facts and reasoning. Do you find this convincing? Why or why not?
6. How can the same idea that makes LLMs powerful (predicting *likely* text) also explain why they sometimes state false things confidently?

### Exercise

**Part A -- Be the language model.** Write down five incomplete sentences (each missing its final word). For each one, list the **three most likely** next words and **one absurdly unlikely** word. Then, beside each, jot down *what kind of knowledge* you used to decide (a fact? grammar? logic? culture?). This is exactly the mixture of knowledge a real model absorbs.

**Part B -- Feel the memory problem.** Write one sentence at least 15 words long where the correct final word depends on a clue near the very beginning (like the "France ... French" example). Explain why a goldfish-memory n-gram model would fail on your sentence but a Transformer would succeed.

**Part C -- Reflect.** In two or three sentences, explain to an imaginary friend why "just predicting the next word," repeated billions of times on huge text, can produce something that appears to understand the world.

---

## Chapter 3: Understanding Tokens

Before an LLM can work with your text, it must first chop that text into pieces it can handle. These pieces are called **tokens.** Understanding tokens is one of the most practical things you can learn about LLMs.

### 3.1 What Are Tokens?

A **token** is a small chunk of text -- it might be a whole word, a part of a word, or even a single character or punctuation mark. LLMs do not read letters or words the way we do; they read and process **tokens.**

**Analogy: Lego blocks.** Imagine you want to build anything out of Lego. You do not have one giant pre-built block for every possible object. Instead, you have a set of standard small blocks, and you combine them to build castles, cars, or spaceships. Tokens are the Lego blocks of language: a fixed set of reusable pieces that combine to form any text.

**Why not just use whole words?** Because there are millions of words across all languages, plus names, typos, slang, and new words invented daily. Storing every possible word is wasteful and still misses new ones. By breaking words into smaller reusable pieces, a model can handle even words it has never seen before -- just like Lego blocks can build a model that did not exist in the instructions.

### 3.2 How Text is Converted into Tokens

The process of splitting text into tokens is called **tokenization.** A component called a **tokenizer** does this job before the text ever reaches the model.

**Example:** The sentence "I love pizza!" might become these tokens:

```
"I love pizza!"   ->   ["I", " love", " pizza", "!"]
                         tok1   tok2     tok3    tok4
```

Notice that the space is often kept attached to the front of a word (" love" with a leading space). This helps the model know where words begin.

Each token is then converted into a **number (an ID)**, because computers work with numbers, not letters:

```
["I", " love", " pizza", "!"]
  |       |        |       |
 40     1842     7693    12      <- token IDs (just example numbers)
```

So when you send text to an LLM, here is the hidden pipeline:

```
Your text  ->  Tokenizer  ->  Tokens  ->  Token IDs (numbers)  ->  Model
"I love pizza!"             ["I"," love"...]   [40, 1842, ...]
```

### 3.3 Tokenization Techniques

There are three main ways to split text into tokens. Each has trade-offs.

**1. Word-level tokenization**

Split text by words.

```
"I love programming"  ->  ["I", "love", "programming"]
```

- **Pro:** simple and intuitive.
- **Con:** the vocabulary becomes enormous (every word needs its own entry), and brand-new or misspelled words ("programmingg") break it because they are not in the list.

**2. Character-level tokenization**

Split text into individual characters.

```
"cat"  ->  ["c", "a", "t"]
```

- **Pro:** tiny vocabulary (just letters, digits, symbols); never confused by new words.
- **Con:** sequences become very long, and each character carries little meaning, so the model works harder to find patterns.

**3. Subword tokenization (the modern winner)**

Split words into meaningful sub-pieces. Common words stay whole; rare words break into parts.

```
"unhappiness"  ->  ["un", "happi", "ness"]
"tokenization" ->  ["token", "ization"]
"cat"          ->  ["cat"]            (common word, stays whole)
```

- **Pro:** balances both worlds -- a reasonable vocabulary size, and it can build any word (even new ones) from sub-pieces.
- **Con:** slightly less intuitive for humans to read.

This is why subword tokenization (using methods with names like **BPE -- Byte Pair Encoding** or **WordPiece**) is what almost all modern LLMs use. It is the Lego-block approach in action.

### 3.4 Why Tokens Matter in LLMs

Tokens are not just an internal detail -- they affect you directly in three practical ways:

**1. Cost.** When you use an LLM through an API, you usually **pay per token.** More tokens in your question and the answer means higher cost.

**2. Limits (the context window).** Every model can only handle a certain number of tokens at once -- this is its **context window** (more in Chapter 4). If your document is too long, it must be shortened or split.

**3. Speed.** More tokens take more time to process. Shorter prompts generally get faster answers.

**Rough rule of thumb for English:**
- 1 token is about 4 characters.
- 1 token is roughly 3/4 of a word.
- So 100 tokens is about 75 words, and 1,000 tokens is about 750 words (a page and a half).

### 3.5 Examples of Tokenization

Let us look at a few real-feeling examples to build intuition:

```
"Hello"         ->  ["Hello"]                        (1 token)
"Hello, world!" ->  ["Hello", ",", " world", "!"]    (4 tokens)
"unbelievable"  ->  ["un", "believ", "able"]          (3 tokens)
"ChatGPT"       ->  ["Chat", "GPT"]                   (2 tokens)
"2024"          ->  ["202", "4"]                      (numbers can split oddly)
"     "         ->  multiple space tokens             (whitespace counts too!)
```

**Surprising lesson:** Even spaces and punctuation are tokens. A long document full of formatting can use more tokens than you expect. And different languages tokenize differently -- English is usually efficient, while some other languages need more tokens for the same meaning.

### Chapter 3 Summary

- A **token** is a chunk of text (a word, part of a word, or a character) -- the Lego block of language.
- **Tokenization** splits your text into tokens, which are then turned into numbers (IDs).
- Three techniques: **word-level** (big vocabulary), **character-level** (long sequences), and **subword** (the modern balance).
- Tokens matter because they drive **cost, length limits, and speed.**
- Rule of thumb: 1 token is roughly 4 characters or 3/4 of a word.

### Reflection Questions

1. Why can subword tokenization handle a word the model has never seen before?
2. If you are paying per token, why might a shorter prompt save money?
3. Why do you think spaces and punctuation are counted as tokens?

### Exercise

Take the sentence "Tokenization is surprisingly fun!" and guess how you would split it into subword tokens. Count them. Then estimate the word count using the "3/4 of a word per token" rule and compare.

---

## Chapter 4: Core Concepts Behind LLMs

Now that we understand tokens, let us explore the key ideas that let an LLM actually *understand* and *connect* those tokens: embeddings, context, and attention. These three concepts are the heart of how LLMs work.

### 4.1 What Are Embeddings?

We learned that each token becomes a number (an ID). But a plain ID like "1842" carries no meaning -- it is just a label, like a jersey number. The model needs a way to capture what a token actually *means.* That is the job of an **embedding.**

An **embedding** turns each token into a **list of numbers (a vector)** that captures its meaning. Tokens with similar meanings get similar lists of numbers.

**Analogy: a map of meaning.** Imagine a giant map where every word is placed at a location. Words with related meanings sit close together. "King" and "queen" are neighbours. "Cat" and "dog" are in the same animal neighbourhood. "Banana" is far away in the fruit district. An embedding is basically the **coordinates** of a word on this map of meaning.

```
A MAP OF MEANING (simplified to 2 directions)

   royalty
      ^
      |   king   queen
      |     .      .
      |
      |                 cat   dog
      |                  .     .       (animals cluster together)
      |
      |   apple  banana
      |    .       .                   (fruits cluster together)
      +-------------------------------> everyday objects
```

The real magic: because meaning becomes numbers, the model can do "meaning math." A famous example:

```
   king - man + woman  =  queen
```

The model can move along the "royalty" direction and the "gender" direction on the map. This shows the embeddings have genuinely captured relationships, not just labels.

**Why this matters:** Embeddings are why an LLM understands that "happy" and "joyful" are similar, even though they are spelled completely differently. The model compares their *positions on the map of meaning,* not their letters.

### 4.2 What is Context and the Context Window?

**Context** is all the text the model is currently looking at to make its prediction -- your question, the conversation so far, any documents you pasted. The model uses this context to decide what to say next.

The same word can mean different things depending on context, and the model handles this:

```
"I went to the bank to deposit money."   (bank = financial institution)
"I sat on the river bank and relaxed."   (bank = edge of a river)
```

The model figures out which "bank" you mean by looking at the surrounding words. That is context at work.

The **context window** is the **maximum number of tokens** the model can consider at one time. Think of it as the model's short-term memory or its "field of view."

**Analogy: a desk.** Imagine working at a desk that can only hold a limited number of pages at once. If you pile on too many pages, the oldest ones fall off the edge. The context window is the size of that desk. Once a conversation gets longer than the window, the earliest parts "fall off" and the model can no longer see them.

```
CONTEXT WINDOW = the model's desk

  [ page 1 ][ page 2 ][ page 3 ] ... [ page N ]
  |<------------ fits on the desk ------------>|
  
  Add too many pages -> the earliest ones fall off and are forgotten.
```

**Practical impact:**
- Small window (a few thousand tokens): good for short chats.
- Large window (hundreds of thousands of tokens): can read entire books at once.
- This is why a very long conversation can make a chatbot "forget" something you said at the very beginning.

### 4.3 What is Attention?

Here is the single most important idea in modern LLMs. **Attention** is the mechanism that lets the model decide **which other words to focus on** when processing each word.

Not all words in a sentence are equally relevant to each other. Attention lets the model weigh the importance of every other word when interpreting the current one.

**Analogy: reading with a highlighter.** When you read a tricky sentence and reach a confusing word, your eyes dart back to earlier words that help explain it. You are "paying attention" to the relevant clues and ignoring the irrelevant ones. Attention gives the model this same ability.

**Example:** Consider the sentence:

> "The animal didn't cross the street because **it** was too tired."

What does "it" refer to -- the animal or the street? You instantly know "it" means the animal (streets do not get tired). Attention lets the model make this same connection by focusing strongly on "animal" when it processes "it."

```
ATTENTION: where does "it" look?

  The  animal  didn't  cross  the  street  because  IT  was tired
        ^^^^^^                                       ||
        strong focus  <------------------------------+
        (weak focus on all the other words)
```

### 4.4 The Intuition Behind "Self-Attention"

The full name of the mechanism is **self-attention.** The "self" means the sentence is **looking at itself** -- every word checks every other word in the same text to figure out relationships.

For each word, self-attention asks three practical questions about all the other words:
1. "Which words here are relevant to me?"
2. "How much should I focus on each of them?"
3. "What information should I pull from them to understand myself better?"

It does this **for every word, in parallel, all at once.** That is what makes Transformers fast and powerful -- they do not crawl word by word; they compare everything to everything in one big step.

**Analogy: a group meeting.** Imagine everyone in a room can instantly hear and weigh what everyone else is saying, deciding in real time whose input matters most for the decision at hand. Self-attention is that meeting, happening for every word simultaneously.

### 4.5 Why Transformers Replaced Older Models

Let us connect this back to Chapter 2's history. Older models (RNNs) read text **one word at a time,** carrying a memory forward. This had two problems:
1. **Slow:** processing had to happen in sequence, one step after another.
2. **Forgetful:** information from early words faded by the time the model reached the end of a long passage.

Transformers, powered by self-attention, fixed both:
1. **Fast:** they look at all words at once (in parallel), which suits modern hardware perfectly.
2. **Great memory:** any word can directly attend to any other word, no matter how far apart, so distant clues are never lost.

```
OLD WAY (RNN)              NEW WAY (Transformer)

word1 -> word2 -> word3    word1  word2  word3
  (one at a time,           \      |     /
   memory fades)             all connected to all,
                             all at once, nothing fades
```

This combination -- speed plus excellent long-range memory -- is exactly why Transformers took over and made today's LLMs possible.

### Chapter 4 Summary

- **Embeddings** turn tokens into lists of numbers that capture meaning; similar words sit close on a "map of meaning."
- **Context** is everything the model currently sees; it uses context to resolve ambiguous words.
- **The context window** is the maximum tokens the model can hold at once -- its short-term memory or "desk size."
- **Attention** lets the model focus on the most relevant words when interpreting each word.
- **Self-attention** has every word examine every other word, all at once -- fast and powerful.
- **Transformers** replaced older models because they are both faster and far better at long-range memory.

### Reflection Questions

1. Why is turning words into numbers (embeddings) more useful than just giving each word a random ID?
2. Why might a chatbot "forget" the start of a very long conversation?
3. In the sentence "The trophy didn't fit in the suitcase because it was too big," what does "it" refer to, and how would attention help?

### Exercise

Pick any sentence with the word "it" in it. Identify what "it" refers to, then list which earlier words you "paid attention to" in order to decide. You just did self-attention by hand.

---

## Chapter 5: Transformer Architecture

In Chapter 4 we met self-attention, the engine of modern LLMs. Now let us look at the full machine it powers: the **Transformer.** Do not worry -- we will keep it visual and intuitive, with no heavy math.

### 5.1 A Simple Explanation of Transformers

A **Transformer** is the neural network design introduced in a famous 2017 research paper titled "Attention Is All You Need." It is the foundation of GPT, BERT, Claude, Gemini, LLaMA, and essentially every major LLM today.

**The core idea in one sentence:** A Transformer reads all the tokens at once, uses self-attention to figure out how they relate, and stacks this process in many layers to build deep understanding.

**Analogy: an expert editing team.** Imagine a document passed through many floors of an office building. On each floor, a team reads the *whole* document, discusses how the parts connect (attention), and adds richer notes in the margins. By the top floor, the document is deeply understood. Each floor is a **Transformer layer**, and modern LLMs have dozens or even over a hundred of these floors.

```
A TRANSFORMER (stacked layers)

   Output (predictions)
        ^
   [  Layer N  ]   <- deepest understanding
        ^
       ...
        ^
   [  Layer 2  ]   <- combines patterns into meaning
        ^
   [  Layer 1  ]   <- notices basic relationships
        ^
   Input tokens (as embeddings)
```

### 5.2 Encoder vs Decoder

The original Transformer had two main parts: an **encoder** and a **decoder.** Understanding the difference explains the different *types* of LLMs we will meet in Chapter 6.

**The Encoder: the "understander."**
The encoder's job is to **read and understand** an entire input. It looks at the whole text at once, in both directions (left-to-right and right-to-left), to build a rich understanding of meaning.

- Best for tasks where you need to *comprehend* text: classification, search, sentiment analysis.
- Example model: BERT.

**The Decoder: the "writer."**
The decoder's job is to **generate** text, one token at a time. Crucially, it can only look **backwards** at the words it has already produced -- it cannot peek at future words (because they do not exist yet).

- Best for tasks where you need to *produce* text: chat, writing, completion.
- Example model: GPT.

```
ENCODER (reads both directions)      DECODER (writes left to right)

  word1 <-> word2 <-> word3            word1 -> word2 -> word3 -> ?
  (sees everything, understands)       (only sees the past, predicts next)
```

**Analogy:**
- The **encoder** is like a **reader** absorbing a whole paragraph to grasp its meaning.
- The **decoder** is like a **writer** composing a sentence word by word, only able to build on what has already been written.

### 5.3 The Attention Mechanism Explained Visually (in Words)

Let us walk through what happens inside a single attention step, slowly and visually.

Imagine the sentence: **"The cat sat on the mat."**

The model wants to deeply understand the word **"sat."** Here is what attention does:

1. **Look around:** "sat" looks at every other word -- "The," "cat," "on," "the," "mat."
2. **Score relevance:** It scores how related each word is to "sat." "Cat" scores high (who sat?). "Mat" scores high (sat where?). "The" scores low (not very informative).
3. **Focus accordingly:** It pays strong attention to "cat" and "mat," weak attention to "The."
4. **Blend information:** It pulls meaning from the high-scoring words and blends it into a richer understanding of "sat" -- now "sat" carries the idea of *a cat sitting on a mat.*

```
Understanding "sat" via attention:

   The   cat    sat    on   the   mat
    .    HIGH    *     low   .    HIGH
         |             |          |
         +----- focus blended into "sat" -----+

   Result: "sat" now means "a cat performing sitting onto a mat"
```

This happens for **every word simultaneously,** and across **many layers,** so understanding gets richer and richer as it flows up the stack.

### 5.4 Key Components: Queries, Keys, and Values

Inside attention, each word creates three things. These have technical names, but the intuition is simple. We will use a **library** analogy.

Imagine you are in a library looking for information:

- **Query (Q):** what you are *looking for.* ("I want books about space.")
- **Key (K):** the *label* on each book that says what it is about. ("This book is about space," "this one is about cooking.")
- **Value (V):** the actual *content* inside each book -- the information you take away.

**How they work together:**
1. Your **Query** ("space") is compared against every book's **Key** (its topic label).
2. Books whose Keys match your Query well get a high score.
3. You then take the **Values** (contents) mostly from the high-scoring books, blended together.

In a sentence, every word sends out a Query ("what am I looking for to understand myself?"), every word offers a Key ("here is what I am about"), and every word holds a Value ("here is the information I can give you"). Attention matches Queries to Keys, then collects the matching Values.

```
QUERY / KEY / VALUE (library analogy)

  Word "sat" sends a QUERY:  "who and where?"
        |
        v   compares against the KEYS of every word
  "cat" KEY: "I'm a subject/animal"   -> strong match!
  "mat" KEY: "I'm a place/object"     -> strong match!
  "the" KEY: "I'm just an article"    -> weak match
        |
        v   collect VALUES from strong matches
  "sat" pulls meaning from "cat" and "mat"
```

That is the entire secret of attention: **Queries search, Keys advertise, Values deliver.** Stack this across many words and many layers, and you get the deep language understanding that makes LLMs work.

### Chapter 5 Summary

- A **Transformer** reads all tokens at once, uses self-attention to relate them, and stacks many layers to build deep understanding.
- The **encoder** understands input (reads both directions); the **decoder** generates output (reads only the past).
- Attention works by **scoring relevance** between words and **blending** information from the most relevant ones.
- **Queries, Keys, and Values** are the three roles in attention: Queries search, Keys advertise, Values deliver -- just like finding books in a library.

### Reflection Questions

1. Why can a decoder only look at past words and not future ones?
2. In the library analogy, what is the difference between a Key and a Value?
3. Why does stacking many layers make understanding richer?

### Exercise

Take the sentence "She poured the water into the glass until it was full." Pretend you are the word "full" using attention. Which words would you score as highly relevant, and why? Write down your Query in plain English.

---

## Chapter 6: Types of LLM Models

In Chapter 5 we met the encoder (the understander) and the decoder (the writer). Different LLMs are built using these parts in different combinations. This gives us three families of models, each suited to different jobs.

### 6.1 Encoder-Only Models (e.g., BERT)

**Encoder-only** models use just the understanding half of the Transformer. They read the entire text at once, in both directions, to build a deep understanding -- but they are **not designed to generate long text.**

**Key trait: bidirectional.** Because they read both directions, they understand each word using clues from *before and after* it. This makes them excellent at comprehension.

**Analogy:** An encoder-only model is like a **careful reader and analyst.** Give it a paragraph and it can tell you the topic, the sentiment, or pull out names -- but you would not ask it to write you a novel.

**Famous example: BERT** (from Google, 2018). The name stands for Bidirectional Encoder Representations from Transformers.

**Best used for:**
- Sentiment analysis ("Is this review positive or negative?")
- Search and ranking ("Which document best matches this query?")
- Named entity recognition ("Find all the people and places in this text.")
- Text classification ("Is this email spam?")

### 6.2 Decoder-Only Models (e.g., GPT)

**Decoder-only** models use just the generating half of the Transformer. They produce text one token at a time, each new token based on all the tokens before it.

**Key trait: one-directional (left-to-right).** They only look backwards at what has been written so far, which is exactly what you need to *generate* fluent text.

**Analogy:** A decoder-only model is like a **gifted storyteller** who continues any story you start, one word at a time, always building naturally on what came before.

**Famous example: GPT** (from OpenAI). The name stands for Generative Pre-trained Transformer. ChatGPT, Claude, Gemini, and LLaMA are all decoder-only style models. This is the dominant design for today's chat assistants.

**Best used for:**
- Chat and conversation
- Writing essays, emails, stories, and code
- Summarisation and translation
- Answering open-ended questions

### 6.3 Encoder-Decoder Models (e.g., T5)

**Encoder-decoder** models use **both** halves. The encoder first reads and understands the entire input; then the decoder generates an output based on that understanding.

**Key trait: transform input into output.** This design shines when the output is a *transformation* of a specific input, rather than open-ended generation.

**Analogy:** An encoder-decoder model is like a **translator.** First they fully listen to and understand a sentence in French (encoder), then they speak the equivalent sentence in English (decoder). Understanding comes first, generation second.

**Famous example: T5** (from Google). The name stands for Text-To-Text Transfer Transformer. It treats every task as "text in, text out."

**Best used for:**
- Translation (English to French)
- Summarisation (long article to short summary)
- Question answering where the answer is drawn from a given passage

### 6.4 Differences, Strengths, and Use Cases

Here is a side-by-side comparison to lock it in:

```
+------------------+---------------------+-----------------------+--------------------------+
| Type             | Reads...            | Best at...            | Example & Use            |
+------------------+---------------------+-----------------------+--------------------------+
| Encoder-only     | Both directions     | Understanding text    | BERT: search, sentiment, |
|                  | (bidirectional)     |                       | classification           |
+------------------+---------------------+-----------------------+--------------------------+
| Decoder-only     | Backwards only      | Generating text       | GPT: chat, writing,      |
|                  | (left-to-right)     |                       | coding, summaries        |
+------------------+---------------------+-----------------------+--------------------------+
| Encoder-decoder  | Encoder both ways,  | Transforming input    | T5: translation,         |
|                  | decoder backwards   | into output           | summarisation            |
+------------------+---------------------+-----------------------+--------------------------+
```

**A simple way to choose:**
- Need to **understand or classify** existing text? Think **encoder-only** (BERT).
- Need to **generate** free-flowing text or chat? Think **decoder-only** (GPT).
- Need to **convert** a specific input into a specific output? Think **encoder-decoder** (T5).

**Why decoder-only models dominate today:** It turned out that decoder-only models, when made very large and trained on enough data, became surprisingly good at *everything* -- including understanding tasks -- just by framing them as text generation. ("Classify this review: ..." -> the model generates "Positive.") This flexibility is why ChatGPT-style models took over the spotlight.

### Chapter 6 Summary

- **Encoder-only** (BERT): reads both directions, great at understanding -- search, sentiment, classification.
- **Decoder-only** (GPT): writes left-to-right, great at generating -- chat, writing, code.
- **Encoder-decoder** (T5): understands then generates, great at transforming input to output -- translation, summarisation.
- Choose based on the task: understand, generate, or transform.
- **Decoder-only** models dominate today because, at large scale, they handle nearly all tasks well.

### Reflection Questions

1. Why is a bidirectional (encoder) model better for sentiment analysis than for writing a story?
2. Why is a decoder-only model the natural choice for a chatbot?
3. Why might a translation task suit an encoder-decoder design?

### Exercise

For each task below, decide which model type fits best (encoder-only, decoder-only, or encoder-decoder) and explain why in one sentence:
(a) Detecting whether a tweet is positive or negative.
(b) Writing a poem about the ocean.
(c) Translating a recipe from Italian to English.

---

## Chapter 7: Popular and Frequently Used LLMs

You have probably heard names like GPT, BERT, LLaMA, Claude, and Gemini. They can feel like alphabet soup at first. But now that you understand the three model families from Chapter 6 (encoder-only, decoder-only, and encoder-decoder), these names will finally click into place.

This is a long chapter, and that is on purpose. Think of it as a **guided tour of a museum of famous AI models.** We will walk slowly from room to room, meeting each important model, learning its story, what makes it special, where you would actually use it, and how it compares to its neighbours. By the end, you will be able to read any AI news headline and understand exactly what model they are talking about and why it matters.

Take your time. You do not need to memorise every detail -- the goal is to build a clear mental map of the LLM landscape.

### 7.1 First, How to Tell Models Apart

Before meeting individual models, let us learn the **seven questions** you can ask about any LLM. These are the "lenses" we will use throughout the chapter. Once you know these, comparing models becomes easy.

```
THE 7 QUESTIONS TO ASK ABOUT ANY MODEL

  1. WHO made it?          (OpenAI, Google, Meta, Anthropic...)
  2. WHAT type is it?      (encoder-only / decoder-only / encoder-decoder)
  3. HOW BIG is it?        (parameters: millions? billions? trillions?)
  4. OPEN or CLOSED?       (can you download it, or only rent it via an API?)
  5. CONTEXT WINDOW?       (how much text can it read at once?)
  6. MULTIMODAL?           (text only, or also images / audio / video?)
  7. WHAT is it best at?   (chat, coding, reasoning, search, translation?)
```

Let us quickly unpack the two that confuse beginners the most.

**"Parameters" -- the size of a model.** Remember from Chapter 1 that a model has billions of internal "knobs" called parameters. When people say a model is "7B" or "70B" or "405B," the B means **billion parameters.** More parameters generally means a smarter, more capable model -- but also one that is slower, more expensive, and needs more powerful computers to run.

**Analogy:** Parameters are like the number of brain cells. More usually means more capability, but it also means the brain needs more food (computing power) to operate.

**"Open vs Closed" -- can you have the model, or only borrow it?**
- **Closed (or "proprietary") models** like GPT-4 and Claude live on the company's servers. You can *use* them by sending requests over the internet (an API), but you can never download the actual model. You are renting, not owning.
- **Open (or "open-weight") models** like LLaMA and Mistral can be **downloaded** and run on your own computer or server. You own a copy. This matters for privacy, cost control, and customisation.

**Analogy:** A closed model is like **Netflix** -- you can watch the movies, but you never get the film reel. An open model is like **buying the DVD** -- it is yours to play, copy, and even remix.

Now, with these seven lenses ready, let us begin the tour.

### 7.2 The GPT Family (OpenAI) -- The Models That Started the Revolution

**GPT** stands for **Generative Pre-trained Transformer.** Let us decode that name, because it perfectly describes the model:
- **Generative:** it *generates* (creates) new text.
- **Pre-trained:** it was trained in advance on enormous amounts of text (Chapter 8).
- **Transformer:** it is built on the Transformer architecture (Chapter 5).

GPT is a **decoder-only** model -- a "writer" that produces text one token at a time. It is made by **OpenAI**, and it is the family behind **ChatGPT**, the app that introduced the world to LLMs.

**The story, version by version.** GPT did not appear fully formed. It grew up across several generations, each dramatically more capable than the last:

```
THE GPT TIMELINE (a story of rapid growth)

  GPT-1  (2018)  ~ 117 million parameters  -- a promising experiment
  GPT-2  (2019)  ~ 1.5 billion parameters  -- first "wow, this writes well!"
  GPT-3  (2020)  ~ 175 billion parameters  -- shocked the world with fluency
  GPT-3.5(2022)  -- the engine behind the original ChatGPT launch
  GPT-4  (2023)  -- far smarter reasoning; accepts images too
  GPT-4o (2024)  -- "omni": fast, cheaper, handles text + image + audio
  o-series(2024+) -- models that "think" longer for hard reasoning
```

**What makes the GPT family special:**
- **All-rounder.** GPT is the Swiss Army knife of LLMs -- excellent at writing, coding, summarising, answering questions, and casual conversation. If you only ever learned one model, this would be it.
- **Great at following instructions.** Thanks to heavy fine-tuning and RLHF (Chapter 8), it does what you ask in a friendly, helpful way.
- **Multimodal (newer versions).** GPT-4o can *see* images and *hear* audio, not just read text. You can show it a photo of your fridge and ask for recipe ideas.
- **The reasoning "o-series."** Newer OpenAI models (like the o1 and o3 line) are trained to **pause and think step by step** before answering, making them much stronger at hard math, logic, and coding puzzles. The trade-off: they are slower and cost more, because "thinking longer" uses more tokens.

**Analogy:** If GPT-3 was a brilliant, fast-talking student who blurts out the first good answer, the reasoning o-series is the same student who now takes a breath, works through the problem on scratch paper, and then gives a more careful answer.

**Where you meet GPT:** ChatGPT (web and app), Microsoft Copilot (in Windows and Office), GitHub Copilot (coding help), and thousands of products built on the OpenAI API.

**One thing to remember:** GPT models are **closed.** You cannot download them; you use them through OpenAI's service. This gives you cutting-edge quality with zero setup, but your data leaves your computer to be processed on OpenAI's servers, and you pay per token.

### 7.3 The BERT Family (Google) -- The Quiet Genius Behind Search

**BERT** stands for **Bidirectional Encoder Representations from Transformers.** It was released by **Google in 2018**, and it changed how computers understand language -- even though most people have never knowingly "used" it.

BERT is an **encoder-only** model -- an "understander," not a "writer." Remember from Chapter 6: encoders read text in **both directions at once.** This is exactly where BERT's name comes from: **Bidirectional.**

**Why bidirectional reading is a big deal.** Consider this sentence with a missing word:

```
"I went to the river ____ to catch some fish."
```

To guess the blank, it helps enormously to see the words *both before and after* it ("river" before, "to catch some fish" after). BERT reads in both directions, so it has the full picture. A left-to-right writer like GPT only sees what came before. This "see everything" ability makes BERT outstanding at **understanding** meaning.

**How BERT was trained -- the "fill in the blank" game.** BERT learned by playing a clever game called **masked language modelling.** Google hid (masked) random words in sentences and asked BERT to guess them:

```
Input:  "The cat sat on the [MASK]."
BERT:   "mat"  (it learned to use both sides for context)
```

By playing this fill-in-the-blank game billions of times, BERT became a deep understander of language.

**What makes BERT special:**
- **Comprehension champion.** It is superb at grasping the meaning, topic, and sentiment of text.
- **Not a chatbot.** BERT does not write essays or hold conversations. Asking BERT to write a story is like asking a brilliant literary critic to compose a symphony -- wrong tool for the job.
- **Powers Google Search.** When you type a messy question into Google, BERT helps the search engine understand what you *actually mean*, not just the keywords.

**The BERT relatives.** BERT was so successful that researchers created improved versions:
- **RoBERTa (Meta):** "Robustly optimised BERT" -- same idea, trained more carefully for better results.
- **DistilBERT:** a smaller, faster, lighter version that keeps most of BERT's ability -- great when you need speed or want to run on modest hardware. (The "Distil" comes from "distillation," squeezing a big model's knowledge into a small one.)
- **ALBERT:** "A Lite BERT" -- a memory-efficient version.

**Where you meet BERT:** Behind the scenes in search engines, spam filters, sentiment-analysis tools (rating reviews as positive/negative), and systems that pull names, dates, and places out of documents. You rarely "chat" with BERT, but it works hard in the background of the internet.

**Open or closed?** BERT is **open** -- anyone can download and use it. This is one reason it became the workhorse of so many real-world text-understanding systems.

### 7.4 The T5 Family (Google) -- One Format to Rule Them All

**T5** stands for **Text-To-Text Transfer Transformer**, from **Google**. It is an **encoder-decoder** model -- it uses *both* halves of the Transformer: the encoder understands the input, then the decoder writes the output.

**The big idea: everything is "text in, text out."** Before T5, different language tasks were handled in different ways. T5's elegant insight was to frame **every single task** as taking text in and producing text out. You just tell it what to do using a little instruction at the start:

```
T5 TURNS EVERYTHING INTO TEXT-TO-TEXT

  Translation:    "translate English to German: Hello"   -> "Hallo"
  Summarising:    "summarize: <long article>"            -> "<short summary>"
  Grading:        "is this grammatical: I has a cat"      -> "no"
  Q&A:            "question: capital of France?"          -> "Paris"
```

**Analogy:** Imagine a universal office assistant who handles every request the same way: you hand them a sticky note describing the task, and they hand back a sticky note with the result. Translate, summarise, answer -- same simple in-and-out process every time. That simplicity was T5's gift to the field.

**What makes T5 special:**
- **Unified and clean.** One model, one format, many tasks. This influenced how later models were designed and prompted.
- **Strong at transformation tasks.** Because it fully understands the input first (encoder) and then writes (decoder), it shines when the output is a *transformation* of a specific input -- like translation or summarisation.

**A famous relative: Flan-T5.** Google later fine-tuned T5 on a huge collection of instructions to create **Flan-T5**, which is much better at following commands -- an early step toward the instruction-following assistants we love today.

**Where you meet T5:** Translation systems, summarisation tools, and many research and enterprise projects. Like BERT, T5 is **open** and widely used by developers.

### 7.5 The LLaMA Family (Meta) -- The Model That Set the Community Free

**LLaMA** stands for **Large Language Model Meta AI**, from **Meta** (the company behind Facebook, Instagram, and WhatsApp). First released in **2023**, it is a **decoder-only** model -- a "writer" like GPT.

But LLaMA's importance is less about its architecture and more about a single game-changing decision: Meta made it **open-weight.** Anyone could download the actual model and run it on their own hardware.

**Why this was revolutionary.** Before LLaMA, the most powerful models (like GPT-4) were locked behind company APIs. If you wanted top-tier AI, you had to rent it and send your data away. LLaMA cracked that door wide open. Suddenly, students, researchers, startups, and hobbyists could run a powerful model **on their own laptop or server**, for free, with full privacy.

**Analogy:** LLaMA was like a famous chef publishing their secret recipe. Overnight, thousands of home cooks and small restaurants could make the dish themselves, tweak it, and invent new variations. An entire ecosystem exploded.

**The LLaMA timeline:**
```
  LLaMA 1   (early 2023)  -- research release; sparked huge excitement
  LLaMA 2   (mid 2023)    -- free for commercial use; widely adopted
  LLaMA 3   (2024)        -- much stronger; sizes like 8B and 70B
  LLaMA 3.1 (2024)        -- a giant 405B version rivalling top closed models
  LLaMA 3.2 (2024)        -- smaller multimodal and on-device versions
```

**What makes LLaMA special:**
- **Open-weight freedom.** Download it, run it offline, keep your data private, and customise it for your needs.
- **A whole ecosystem.** Thousands of community models are built on top of LLaMA -- fine-tuned for medicine, law, coding, role-play, specific languages, and more.
- **Efficiency.** LLaMA showed that a smaller, well-trained model can match much larger ones, proving that *how* you train matters as much as raw size.

**Where you meet LLaMA:** Self-hosted AI tools, privacy-focused apps (hospitals, banks, governments that cannot send data to outside servers), and countless open-source projects. Friendly tools like **Ollama** let anyone run LLaMA-based models on a personal computer with a single command.

### 7.6 The Claude Family (Anthropic) -- The Careful, Thoughtful Assistant

**Claude** is made by **Anthropic**, a company founded by former OpenAI researchers with a strong focus on **AI safety.** Claude is a **decoder-only** chat model, a direct competitor to GPT, and is **closed** (used via API or the Claude app).

**What makes Claude special:**
- **Thoughtful, long-form responses.** Claude is known for careful, nuanced, well-structured answers -- many users find it especially good for writing, analysis, and working through complex topics.
- **Very large context windows.** Claude can read extremely long documents at once (hundreds of thousands of tokens -- entire books or large codebases), making it excellent for analysing big files.
- **A safety-first design philosophy.** Anthropic pioneered an approach called **Constitutional AI**, where the model is trained to follow a set of guiding principles (a "constitution") to be helpful, honest, and harmless -- with less reliance on humans labelling every bad example.

**The Claude 3 family -- three sizes named after poetry forms.** Anthropic offers Claude in tiers so you can balance speed, cost, and intelligence:
```
  Claude Haiku   -- smallest, fastest, cheapest (quick simple tasks)
  Claude Sonnet  -- balanced middle option (the everyday workhorse)
  Claude Opus    -- largest, smartest, most capable (hardest tasks)
```
(The names are a nod to types of poems: a haiku is short, an opus is grand.) Later versions like **Claude 3.5 Sonnet** pushed quality even higher, especially for coding.

**Fun fact:** Anthropic also created the **Model Context Protocol (MCP)** -- a standard way for AI assistants to safely connect to external tools and data. You will hear more about this when we discuss AI agents in Chapter 12.

**Where you meet Claude:** The Claude.ai app, and many developer tools and products built on Anthropic's API (including coding assistants).

### 7.7 The Gemini Family (Google) -- Built Multimodal from the Ground Up

**Gemini** is **Google's flagship** modern model family (the successor to its earlier "Bard" assistant and "PaLM" models). It is **decoder-only** style, **closed** (API and app), and was designed from the start to be **multimodal.**

**What makes Gemini special:**
- **Natively multimodal.** While many models added image understanding later, Gemini was built from day one to handle **text, images, audio, and video together.** You can give it a video clip and ask questions about what happens in it.
- **Enormous context windows.** Gemini (especially the 1.5 Pro version) can handle very large amounts of information at once -- up to a million tokens or more -- enough to read lengthy books, hours of audio, or huge code repositories in a single go.
- **Deep Google integration.** It powers AI features across Google Search, Workspace (Docs, Gmail), and Android.

**The Gemini line-up:**
```
  Gemini Nano   -- tiny version that runs on phones, offline
  Gemini Flash  -- fast and cheap for high-volume tasks
  Gemini Pro    -- the powerful all-round model
  Gemini Ultra  -- the largest, most capable tier
```

**Gemma -- Google's open cousins.** Alongside the closed Gemini, Google also releases **Gemma**, a family of **open-weight** models (like LLaMA) that developers can download and run themselves. So Google plays in both the closed (Gemini) and open (Gemma) worlds.

**Where you meet Gemini:** The Gemini app, Google Search's AI overviews, Google Workspace, Android phones, and Google's developer API.

### 7.8 Mistral (Mistral AI) -- Small, Fast, and Surprisingly Mighty

**Mistral AI** is a **European** company (based in France) that became famous for releasing **open-weight** models that punch far above their size -- delivering strong quality while staying small, fast, and cheap to run.

**What makes Mistral special:**
- **Efficiency.** Their flagship small model, **Mistral 7B** (7 billion parameters), stunned everyone by outperforming larger models. Small enough to run on a single good graphics card, yet very capable.
- **A clever trick called "Mixture of Experts" (MoE).** Their **Mixtral** models use a smart design where, for each token, only a few specialised "expert" sub-networks activate instead of the whole model. 

**Analogy for Mixture of Experts:** Imagine a hospital with many specialist doctors. When a patient arrives, you do not wake up *every* doctor -- you call only the two or three relevant specialists. This gives you expert quality while saving enormous effort. MoE models do the same: they have many "experts" but use only the most relevant few for each token, getting big-model quality at small-model speed.

**Where you meet Mistral:** Self-hosted setups, privacy-focused European businesses, the "Le Chat" assistant, and many open-source projects that need a fast, capable, freely available model.

### 7.9 Other Notable Models Worth Knowing

The LLM world is large and growing fast. Here are other important names you will run into, each with a one-line "claim to fame":

- **Grok (xAI):** Elon Musk's company xAI makes Grok, a chat model integrated directly into the **X** (formerly Twitter) platform, with access to real-time posts.

- **DeepSeek (DeepSeek AI, China):** an **open-weight** family that made headlines for **excellent reasoning and coding** at remarkably low training cost. Its reasoning-focused models rival far more expensive ones, showing that clever training can beat brute-force spending.

- **Qwen (Alibaba, China):** a strong, **open-weight** family ("Tongyi Qianwen") with many sizes, excellent multilingual ability (especially Chinese and English), and solid coding and math skills. Very popular in the open-source community.

- **Command R / Command R+ (Cohere):** models built specifically for **business use** and for **RAG** (retrieval-augmented generation -- connecting an LLM to a company's own documents). Cohere focuses on enterprise customers.

- **Phi (Microsoft):** a family of **small but clever** open models. Microsoft showed that training small models on very high-quality, "textbook-like" data produces surprisingly strong reasoning -- proving quality of data can beat sheer quantity.

- **Falcon (TII, UAE):** an early influential **open-weight** model from the United Arab Emirates that helped kick off the open-model movement alongside LLaMA.

- **PaLM (Google):** Google's earlier large model that powered Bard before Gemini arrived. You may still see it referenced historically.

```
A FIELD GUIDE TO "OTHER" MODELS (claim to fame)

  Grok      -> chat built into X, real-time social data
  DeepSeek  -> top-tier reasoning/coding, very low cost, open
  Qwen      -> strong multilingual + coding, many sizes, open
  Command R -> enterprise + document RAG focus
  Phi       -> tiny but smart; quality data over quantity
  Falcon    -> early open-weight pioneer (UAE)
  PaLM      -> Google's pre-Gemini big model (historical)
```

### 7.10 Open vs Closed Models -- A Deeper Look

This distinction matters so much in practice that it deserves its own section. Choosing between open and closed models is one of the first real decisions anyone building with LLMs must make.

**Closed models (GPT, Claude, Gemini) -- the "rent a supercomputer brain" option:**

```
  PROS                                  CONS
  + Usually the highest quality         - Your data leaves your machine
  + Zero setup -- just call the API     - Ongoing cost (pay per token)
  + Always updated by the provider      - No control; model can change
  + No expensive hardware needed        - Cannot run offline / fully private
```

**Open models (LLaMA, Mistral, Gemma, Qwen) -- the "own your brain" option:**

```
  PROS                                  CONS
  + Full privacy (data stays local)     - You need capable hardware
  + Run offline, no per-token fees      - Setup and maintenance is on you
  + Customise / fine-tune freely        - Often (not always) a bit behind
  + Full control; never changes on you    the very best closed models
```

**A simple way to decide:**
- Building a quick product and want the best quality with no fuss? **Closed model (API).**
- Handling sensitive data (medical, legal, financial) that must not leave your servers? **Open model, self-hosted.**
- Need to run on a phone or with no internet? **Small open model.**
- Want to deeply customise the model for a narrow domain? **Open model you can fine-tune.**

### 7.11 How to Choose the Right Model for a Task

Beginners often ask, "Which model is *the best*?" The honest answer is: **it depends on the job.** A motorcycle is not "better" than a truck -- it depends on whether you are commuting or moving furniture. Same with models.

Here is a practical decision guide:

```
WHAT DO YOU NEED TO DO?

  Understand / classify text (sentiment, search, spam)
      -> Encoder-only model (BERT family)

  Chat, write, brainstorm, general assistant
      -> Decoder-only chat model (GPT, Claude, Gemini, LLaMA)

  Translate or summarise a specific input
      -> Encoder-decoder (T5) OR a strong general chat model

  Hard math / logic / step-by-step reasoning
      -> A "reasoning" model (OpenAI o-series, DeepSeek reasoning, etc.)

  Work with images / audio / video
      -> A multimodal model (GPT-4o, Gemini, newer Claude)

  Keep data fully private / run offline
      -> An open-weight model you host (LLaMA, Mistral, Gemma, Qwen)

  Cheapest, fastest for huge volume of simple tasks
      -> A small/fast tier (Claude Haiku, Gemini Flash, Mistral 7B)
```

**Also weigh these three trade-offs every time:**
1. **Quality vs Cost:** the smartest models cost the most. Do not use a giant model for a tiny task.
2. **Speed vs Intelligence:** reasoning models "think longer" and are slower. Use them only when accuracy really matters.
3. **Convenience vs Control:** closed APIs are effortless; open models give you control and privacy but require effort.

### 7.12 Big Comparison Tables

Let us bring the whole tour together in two reference tables you can come back to anytime.

**Table 1 -- The major model families at a glance:**
```
+----------+-------------+----------------+----------------+-------------------------+
| Family   | Maker       | Type           | Open/Closed    | Best known for          |
+----------+-------------+----------------+----------------+-------------------------+
| GPT      | OpenAI      | Decoder-only   | Closed (API)   | All-round chat, coding, |
|          |             |                |                | the model that started  |
|          |             |                |                | the boom                |
+----------+-------------+----------------+----------------+-------------------------+
| BERT     | Google      | Encoder-only   | Open           | Understanding & search; |
|          |             |                |                | not a chatbot           |
+----------+-------------+----------------+----------------+-------------------------+
| T5       | Google      | Encoder-decoder| Open           | "Text-to-text"          |
|          |             |                |                | translation, summaries  |
+----------+-------------+----------------+----------------+-------------------------+
| LLaMA    | Meta        | Decoder-only   | Open-weight    | Self-hosting, privacy,  |
|          |             |                |                | the open ecosystem      |
+----------+-------------+----------------+----------------+-------------------------+
| Claude   | Anthropic   | Decoder-only   | Closed (API)   | Careful long-form,      |
|          |             |                |                | huge context, safety    |
+----------+-------------+----------------+----------------+-------------------------+
| Gemini   | Google      | Decoder-only   | Closed (API)   | Native multimodal,      |
|          |             |                |                | massive context         |
+----------+-------------+----------------+----------------+-------------------------+
| Mistral  | Mistral AI  | Decoder-only   | Open-weight    | Small, fast, efficient; |
|          |             | (+ MoE)        |                | mixture-of-experts      |
+----------+-------------+----------------+----------------+-------------------------+
```

**Table 2 -- The "other" models and their claim to fame:**
```
+-----------+--------------+----------------+-------------------------------+
| Model     | Maker        | Open/Closed    | Claim to fame                 |
+-----------+--------------+----------------+-------------------------------+
| Grok      | xAI          | Mixed          | Built into X, real-time data  |
| DeepSeek  | DeepSeek AI  | Open-weight    | Top reasoning, very low cost  |
| Qwen      | Alibaba      | Open-weight    | Multilingual + coding, sizes  |
| Command R | Cohere       | Mixed          | Enterprise + document RAG     |
| Phi       | Microsoft    | Open-weight    | Tiny but smart; quality data  |
| Falcon    | TII (UAE)    | Open-weight    | Early open-model pioneer      |
| Gemma     | Google       | Open-weight    | Google's open cousin of Gemini|
+-----------+--------------+----------------+-------------------------------+
```

**Table 3 -- Speed/cost tiers (a pattern most providers follow):**
```
+-------------+--------------------------+----------------------------------+
| Tier        | Example names            | Use it for...                    |
+-------------+--------------------------+----------------------------------+
| Tiny/Fast   | Gemini Nano, Phi,        | On-device, simple high-volume    |
|             | Mistral 7B               | tasks, offline use               |
| Balanced    | Claude Sonnet,           | Everyday workhorse: most chat    |
|             | Gemini Flash/Pro         | and writing tasks                |
| Top/Smart   | GPT-4o, Claude Opus,     | Hardest reasoning, best quality, |
|             | Gemini Ultra             | complex coding                   |
| Reasoning   | OpenAI o-series,         | Math, logic, multi-step problems |
|             | DeepSeek reasoning       | where accuracy is critical       |
+-------------+--------------------------+----------------------------------+
```

### 7.13 A Note on How Fast This Changes

One honest warning: this is the **fastest-moving area in all of technology.** New models and versions appear almost every month, and today's "best" model may be overtaken next week. Specific version numbers and parameter counts in this chapter will age quickly.

But here is the good news: **the fundamentals you have learned do not age.** The seven questions from Section 7.1, the three model families from Chapter 6, the open-vs-closed trade-off, and the speed-vs-intelligence-vs-cost balance -- these timeless ideas will let you understand *any* new model that appears. When the next big model launches, just ask the seven questions, and you will instantly know where it fits.

**Analogy:** You do not need to memorise every car model ever made to understand cars. Once you understand engines, wheels, fuel, and trade-offs, you can size up any new car in minutes. The same is true for LLMs.

### Chapter 7 Summary

- Ask **seven questions** about any model: maker, type, size (parameters), open/closed, context window, multimodal, and specialty.
- **GPT** (OpenAI, closed, decoder-only): the all-round model that started the boom; newer versions are multimodal and include "reasoning" models that think step by step.
- **BERT** (Google, open, encoder-only): a comprehension champion that powers search; learns by filling in blanks; not a chatbot. Relatives: RoBERTa, DistilBERT, ALBERT.
- **T5** (Google, open, encoder-decoder): frames every task as "text in, text out"; great for translation and summarising. Relative: Flan-T5.
- **LLaMA** (Meta, open-weight, decoder-only): set the community free by being downloadable; sparked a huge open-source ecosystem.
- **Claude** (Anthropic, closed): careful long-form answers, huge context, safety-first ("Constitutional AI"); tiers named Haiku/Sonnet/Opus.
- **Gemini** (Google, closed): natively multimodal with massive context; has an open cousin called **Gemma**.
- **Mistral** (open-weight): small, fast, efficient; introduced **Mixture of Experts** (use only the relevant "expert" sub-networks per token).
- **Others:** Grok, DeepSeek, Qwen, Command R, Phi, Falcon -- each with a distinct claim to fame.
- **Open vs closed** is a key practical choice (privacy/control vs convenience/quality).
- There is no single "best" model -- choose based on the **task** and the trade-offs of quality, speed, and cost.
- The field changes fast, but the **fundamentals let you understand any new model.**

### Reflection Questions

1. Using the "seven questions" from Section 7.1, describe GPT and BERT side by side. How do they differ on type, openness, and specialty?
2. Why is BERT excellent for Google Search but a poor choice for writing a bedtime story?
3. What real-world situation would push you to choose an open model (like LLaMA) over a closed one (like GPT-4)?
4. In your own words, explain "Mixture of Experts" using the hospital analogy.
5. Why does the chapter say there is no single "best" model?

### Exercise

You are advising three different people. For each, recommend a model *type* or specific model from this chapter, and explain your choice in one or two sentences:

(a) A hospital that wants an AI to summarise patient notes, but legally **cannot let patient data leave its own servers.**

(b) A student who wants the **smartest possible help** solving tricky calculus problems, and does not mind waiting a few extra seconds for the answer.

(c) A startup building a **simple customer-support chatbot** that must answer thousands of easy questions per day **as cheaply as possible.**

Bonus: For each, name one trade-off (privacy, cost, speed, or quality) that most influenced your recommendation.

---

## Chapter 8: How LLMs Are Trained

We have described *what* LLMs do. Now let us see *how they learn* to do it. Training is the process of adjusting those billions of internal knobs (parameters) until the model becomes good at predicting text.

### 8.1 Training Data

An LLM learns from **text -- lots and lots of text.** Its training data typically includes:
- Websites and articles
- Books
- Encyclopedias like Wikipedia
- Code repositories
- Forums and discussions

We are talking about an amount of text equivalent to **millions of books** -- far more than any human could read in a thousand lifetimes.

**Analogy:** Imagine a student who reads a colossal library, absorbing patterns of grammar, facts, reasoning, and style. They do not memorise every page word-for-word; instead they internalise *how language and ideas tend to flow.* That is what training does.

**A crucial point about quality:** The model learns from whatever it reads -- including mistakes, biases, and falsehoods present in the data. "Garbage in, garbage out" applies. This is why data is carefully filtered and cleaned, and why models can still pick up human biases (more in Chapter 10).

### 8.2 Pretraining vs Fine-Tuning

Training happens in two main stages. Understanding this two-step process clears up a lot of confusion.

**Stage 1: Pretraining (the general education)**

In pretraining, the model reads its enormous pile of text and practises one simple task over and over: **predict the next token.** It guesses the next word, checks the real answer, and nudges its parameters to do better next time -- billions of times.

After pretraining, the model has broad general knowledge and language ability. But it is a bit like a brilliant graduate who knows a lot but has not yet learned how to be *helpful* in conversation. This stage is by far the most expensive and time-consuming (costing millions of dollars and weeks of computing).

**Stage 2: Fine-tuning (the specialised training)**

In fine-tuning, we take the pretrained model and train it further on a smaller, carefully chosen set of examples to shape its behaviour for a specific purpose -- for instance, being a polite, helpful assistant, or specialising in medical or legal text.

```
TRAINING IN TWO STAGES

  [ Huge general text ]              [ Smaller focused examples ]
          |                                    |
          v                                    v
   PRETRAINING  ----------------------->  FINE-TUNING
   (learn language &                      (learn to behave a
    general knowledge)                     certain way / specialise)
          |                                    |
          v                                    v
   A knowledgeable but                  A helpful, well-behaved,
   raw model                            specialised assistant
```

**Analogy:** Pretraining is like a broad **university education** -- wide general knowledge. Fine-tuning is like **on-the-job training** for a specific role -- learning the manners and skills the job requires.

### 8.3 Supervised vs Unsupervised Learning

Two important learning styles show up in training. The difference is whether the data comes with "answers" attached.

**Unsupervised learning (learning from raw data, no labels)**

Here the model learns from plain text with no human-provided answers. Next-token prediction during pretraining is essentially this: the "answer" (the next word) is simply the text itself. No human had to label anything -- the text supplies its own answers. This is why models can train on the whole internet: the data is already there, no labelling needed.

**Analogy:** Like a child learning language just by being immersed in conversation -- nobody hands them a labelled worksheet; they absorb patterns from the world around them.

**Supervised learning (learning from labelled examples)**

Here humans provide example pairs of "input -> correct output." For instance: a question paired with an ideal answer, or a review paired with the label "positive." The model learns to match these examples. Fine-tuning often uses supervised learning with carefully written examples.

**Analogy:** Like a student doing practice problems *with* an answer key, checking each answer and correcting mistakes.

```
+----------------------+-------------------------+----------------------------+
| Style                | Needs human labels?     | Used mostly in...          |
+----------------------+-------------------------+----------------------------+
| Unsupervised         | No (text labels itself) | Pretraining                |
| Supervised           | Yes (input -> answer)   | Fine-tuning                |
+----------------------+-------------------------+----------------------------+
```

### 8.4 Reinforcement Learning with Human Feedback (RLHF)

There is one more famous step that helped turn raw models into pleasant, helpful assistants like ChatGPT: **RLHF -- Reinforcement Learning with Human Feedback.**

The problem it solves: a pretrained model can produce text that is fluent but unhelpful, rude, or unsafe. We want it to prefer answers humans actually like. But "being helpful" is hard to define with a simple rule. So we let **humans guide it by preference.**

**How RLHF works, in three simple steps:**

1. **Collect human preferences.** The model produces several possible answers to a question. Human reviewers rank them from best to worst.
2. **Train a "reward model."** Using these rankings, we train a helper model that can score any answer the way humans would -- giving high scores to helpful answers and low scores to bad ones.
3. **Improve the main model.** The main LLM is then trained to produce answers that earn high scores from the reward model -- effectively learning to please human preferences.

```
RLHF IN PICTURES

  Step 1: Model gives answers  ->  Humans rank them (best..worst)
  Step 2: Rankings  ->  train a REWARD MODEL (scores answers like a human would)
  Step 3: Main model practises  ->  aims for high reward  ->  becomes more helpful
```

**Analogy:** Imagine training a dog. You cannot explain "good behaviour" in words, but you can give treats for good actions and withhold them for bad ones. Over time the dog learns what you like. RLHF gives the model "treats" (rewards) for answers humans prefer.

RLHF is a big reason modern chat assistants feel polite, helpful, and safe rather than just technically fluent.

### Chapter 8 Summary

- LLMs train on **massive amounts of text** -- equivalent to millions of books.
- **Pretraining** teaches broad language and knowledge via next-token prediction (the expensive first stage).
- **Fine-tuning** shapes the model's behaviour for a specific purpose (the focused second stage).
- **Unsupervised learning** uses raw text (no labels); **supervised learning** uses labelled input-output pairs.
- **RLHF** uses human preferences to make models more helpful, polite, and safe.

### Reflection Questions

1. Why can pretraining use unlabelled text from the internet?
2. What is the difference between what pretraining and fine-tuning each accomplish?
3. In the dog-training analogy, what plays the role of the "treat" in RLHF?

### Exercise

Explain to an imaginary friend, in three sentences, why a freshly pretrained model might be smart but still need RLHF before becoming a friendly assistant.

---

## Chapter 9: How LLMs Generate Text

We know the model is trained to predict the next token. But how does that turn into the flowing paragraphs you see in ChatGPT? This chapter reveals the surprisingly simple loop behind text generation -- and the settings that control its style.

### 9.1 Predicting the Next Token, One at a Time

Here is the core secret: an LLM generates text by predicting **one token at a time,** then feeding its own output back in to predict the next, over and over.

**The generation loop:**
1. You give the model some text (your prompt).
2. The model predicts the most suitable **next token.**
3. That token is added to the text.
4. The model now reads the slightly longer text and predicts the **next** token.
5. Repeat until the answer is complete.

```
GENERATING "The sky is blue."

  Input: "The sky is"      -> predicts "blue"   (added)
  Input: "The sky is blue" -> predicts "."      (added)
  Input: "The sky is blue."-> predicts [STOP]   (done)
```

**Analogy:** It is like building a sentence with magnetic words on a fridge, adding one word at a time, each chosen to best fit everything placed so far. The model never plans the whole sentence in advance -- it simply keeps choosing the best next piece. Remarkably, this simple loop produces essays, code, and poems.

**This also explains streaming:** When you watch ChatGPT type out its answer word by word, you are literally watching this loop happen in real time -- each token appears as it is predicted.

### 9.2 How the Next Token is Actually Chosen

At each step, the model does not pick just one word with certainty. Instead, it produces a **probability for every possible token** in its vocabulary.

**Example:** After "The weather today is," the model might output:

```
"sunny"  -> 35% probability
"cloudy" -> 25%
"rainy"  -> 20%
"nice"   -> 10%
"cold"   -> 5%
... (thousands of other tokens with tiny probabilities)
```

Now the model must *choose* one. **How** it chooses is controlled by a few important settings. These settings are why the same prompt can give different answers, and why you can make a model more "creative" or more "focused."

### 9.3 Temperature: The Creativity Dial

**Temperature** controls how *random* or *adventurous* the choice is.

- **Low temperature (e.g., 0):** the model almost always picks the highest-probability token. Output is **focused, predictable, and consistent** -- great for facts, math, and code.
- **High temperature (e.g., 0.9):** the model is more willing to pick less likely tokens. Output is **creative, varied, and surprising** -- great for brainstorming, poetry, and stories.

```
TEMPERATURE DIAL

  Low (0.0) ----------------------------------- High (1.0+)
  "Safe & focused"                          "Creative & wild"
  Always picks the                          Willing to pick
  most likely word                          surprising words
  
  Best for: facts, code                     Best for: stories, ideas
```

**Analogy:** Temperature is like a chef's willingness to improvise. Low temperature follows the recipe exactly every time. High temperature throws in unexpected spices -- sometimes brilliant, sometimes odd.

### 9.4 Top-k and Top-p: Limiting the Choices

Two more settings control *which* tokens are even allowed to be considered.

**Top-k sampling:** Only consider the **k most likely** tokens, ignore the rest.

- Example: top-k = 3 means "only ever choose among the top 3 candidates." This prevents the model from picking bizarre, very unlikely words.

```
TOP-K = 3 (only the top 3 are allowed)

  sunny  35%  <- allowed
  cloudy 25%  <- allowed
  rainy  20%  <- allowed
  ----------------- cutoff -----------------
  nice   10%  <- ignored
  cold    5%  <- ignored
```

**Top-p sampling (also called "nucleus sampling"):** Consider the smallest group of top tokens whose probabilities **add up to p** (e.g., 90%), then choose among those.

- Example: top-p = 0.9 means "include the most likely tokens until their combined probability reaches 90%, then pick from that group." This adapts automatically -- sometimes a few words, sometimes many.

**Analogy:** Top-k is like saying "only consider the 3 most popular dishes on the menu." Top-p is like saying "only consider the dishes that together make up 90% of what people order." Both keep the model away from weird, low-probability choices, while still allowing some variety.

### 9.5 Why Outputs Vary

Now you can understand a common beginner question: *"Why did I get a different answer when I asked the same thing twice?"*

The reason is **sampling.** Unless temperature is set to 0, the model intentionally introduces some randomness in choosing tokens. Two runs of the same prompt walk slightly different paths through the probabilities, producing different (but usually equally valid) answers.

- Want **consistent, repeatable** answers? Use **low temperature (near 0).**
- Want **fresh, varied, creative** answers? Use **higher temperature** and top-p sampling.

This variability is a feature, not a bug -- it is what lets an LLM brainstorm ten different slogans instead of repeating the same one.

### Chapter 9 Summary

- LLMs generate text by predicting **one token at a time** and feeding the result back in -- a simple loop that creates whole essays.
- At each step the model produces a **probability for every possible token.**
- **Temperature** controls creativity: low = focused and consistent; high = creative and varied.
- **Top-k** limits choices to the k most likely tokens; **top-p** limits them to the top tokens summing to probability p.
- Outputs **vary** between runs because of intentional randomness (sampling) -- unless temperature is near 0.

### Reflection Questions

1. Why does watching ChatGPT "type" reveal how generation actually works?
2. If you wanted the exact same answer every time, what temperature would you choose?
3. What is the difference between top-k and top-p in plain words?

### Exercise

Imagine the prompt "Write a tagline for a coffee shop." Predict what would happen at temperature 0 versus temperature 0.9. Which setting would you choose if you wanted ten different creative options, and why?

---

## Chapter 10: Strengths and Limitations of LLMs

LLMs are powerful, but they are not magic, and they are not perfect. To use them wisely -- and to explain them honestly -- you must understand both what they shine at and where they stumble.

### 10.1 What LLMs Are Good At

LLMs genuinely excel at a wide range of language tasks:

- **Writing and editing:** drafting emails, essays, stories; fixing grammar; changing tone.
- **Summarising:** condensing long documents into key points.
- **Explaining:** breaking down complex topics into simple language (great for learning).
- **Translating:** converting text between many languages.
- **Coding:** writing, explaining, and debugging programs.
- **Brainstorming:** generating many ideas quickly.
- **Answering questions:** on an enormous range of general topics.
- **Reformatting:** turning messy notes into clean lists, tables, or structured text.

**Why so versatile?** Because nearly any task can be framed as "text in, text out," and the model learned general language patterns during training. One model handles thousands of different jobs without being separately programmed for each.

### 10.2 Hallucinations

Here is the most important limitation to understand: **LLMs can confidently make things up.** This is called a **hallucination** -- when the model produces information that sounds plausible and authoritative but is simply **false.**

**Why does this happen?** Remember, the model is a *next-token predictor,* not a database of verified facts. Its goal is to produce text that *sounds* right based on patterns -- not necessarily text that *is* right. When it does not actually know something, it does not always say "I don't know." Instead it may generate a fluent, confident-sounding answer that happens to be wrong.

**Examples of hallucinations:**
- Inventing a book title or author that does not exist.
- Making up a fake statistic or date.
- Citing a court case or research paper that was never written.
- Confidently giving a wrong answer to a math problem.

**Analogy:** Imagine a very smooth-talking student who never wants to admit they do not know an answer. They will confidently invent something that *sounds* correct. LLMs can behave this way.

**How to protect yourself:**
- **Verify important facts** from trusted sources -- never trust an LLM blindly for critical information (medical, legal, financial).
- Be especially careful with **specific names, numbers, dates, and citations.**
- Use techniques (like giving the model reference documents to draw from) that reduce hallucination.

### 10.3 Bias

LLMs learn from human-written text, and human text contains **biases** -- unfair assumptions about gender, race, culture, and more. The model can absorb and even repeat these biases.

**Example:** If training text frequently associates certain jobs with one gender, the model might unfairly assume the same -- for instance, defaulting to "he" for doctors and "she" for nurses.

**Why it happens:** The model is a mirror of its training data. "Garbage in, garbage out" applies to fairness too. If society's text contains bias, the model can reflect it.

**What is done about it:** Developers work hard to reduce bias through careful data filtering, fine-tuning, and safety training (like RLHF). But no model is perfectly neutral, so it is wise to stay aware that bias can appear.

### 10.4 Other Limitations of Current Models

Beyond hallucinations and bias, keep these limits in mind:

- **Knowledge cutoff:** A model only knows what was in its training data. It may not know about events after its "knowledge cutoff" date unless it is given fresh information or tools to look things up.
- **No true understanding:** The model manipulates patterns in language; it does not "understand" or "believe" things the way a human does. It has no consciousness, feelings, or genuine intent.
- **Weak at precise math and logic:** Because it predicts tokens rather than calculating, it can make arithmetic and multi-step logic errors (though newer models and tools help a lot).
- **Context window limits:** It can only consider so much text at once (Chapter 4). Very long inputs get cut off or forgotten.
- **Sensitive to wording:** The same question phrased differently can yield different answers. Small prompt changes can matter a lot.
- **No memory between sessions (by default):** Unless the app is built to store history, the model does not remember past conversations once they end.
- **Can be confidently wrong:** It does not reliably signal its own uncertainty.

```
QUICK MAP OF LLM STRENGTHS vs LIMITS

  STRENGTHS                        LIMITS
  + Fluent writing                 - Hallucinations (confident errors)
  + Summarising                    - Bias from training data
  + Explaining simply              - Knowledge cutoff (no recent events)
  + Translation                    - Weak precise math / logic
  + Coding help                    - Context window limits
  + Brainstorming                  - No true understanding / memory
```

### Chapter 10 Summary

- LLMs are excellent at writing, summarising, explaining, translating, coding, and brainstorming.
- **Hallucinations** are confident but false outputs -- always verify important facts.
- **Bias** can be absorbed from human-written training data.
- Other limits: **knowledge cutoff, no true understanding, weak precise math, context limits, sensitivity to wording, and limited memory.**
- Used wisely (with verification), LLMs are powerful; trusted blindly, they can mislead.

### Reflection Questions

1. Why does a next-token predictor sometimes "hallucinate" facts?
2. Where do an LLM's biases come from?
3. Why should you double-check an LLM's answer about a medical or legal question?

### Exercise

Think of a question where an LLM's answer would be safe to trust, and another where you would definitely verify it elsewhere. Explain what makes the two cases different.

---

## Chapter 11: Practical Usage

Theory is great, but how do people actually *use* LLMs in the real world? This chapter shows how developers connect to LLMs and -- most importantly for everyone -- how to write good prompts to get great results.

### 11.1 How Developers Use LLMs: APIs and Prompts

There are two common ways to use an LLM:

**1. Through a chat app (for everyone).** You simply type into ChatGPT, Claude, or Gemini. No technical setup needed.

**2. Through an API (for developers).** An **API (Application Programming Interface)** is a way for one program to talk to another. Developers send the model a request (your text) over the internet and receive the model's response, which their app can then use.

```
HOW AN APP USES AN LLM VIA API

  Your app  --(sends prompt + settings)-->  LLM service
  Your app  <--(receives generated text)--  LLM service

  The app can then display it, store it, or act on it.
```

A developer's request typically includes:
- **The prompt:** the text/instructions you send.
- **Settings:** like temperature, max length (in tokens), and which model to use.
- **An API key:** a secret password that identifies and bills the account.

This is how thousands of products add AI features -- a customer-support chatbot, a writing assistant inside a word processor, or a coding helper inside a code editor are all just apps calling an LLM API behind the scenes.

### 11.2 Prompt Engineering Basics

A **prompt** is the input you give the model. **Prompt engineering** is the skill of writing prompts that get the best possible results. Because the model responds to *what* and *how* you ask, good prompting makes a huge difference.

Here are the core principles, each with a before-and-after example.

**Principle 1: Be specific and clear.**

```
Weak:   "Tell me about dogs."
Strong: "List 5 dog breeds that are good for first-time owners in apartments,
         with one sentence on why each suits apartment living."
```

**Principle 2: Give the model a role or persona.**

```
Weak:   "Explain recursion."
Strong: "You are a patient computer science teacher. Explain recursion to a
         12-year-old using a simple real-life analogy."
```

**Principle 3: Specify the format you want.**

```
Weak:   "Give me a meal plan."
Strong: "Create a 3-day vegetarian meal plan. Present it as a table with
         columns: Day, Breakfast, Lunch, Dinner."
```

**Principle 4: Provide examples (this is called "few-shot" prompting).**

```
Strong: "Classify the sentiment as Positive or Negative.
         Example 1: 'I loved it!' -> Positive
         Example 2: 'Terrible service.' -> Negative
         Now classify: 'The food was cold and late.'"
```

**Principle 5: Give context and constraints.**

```
Weak:   "Write an email."
Strong: "Write a polite email to a client named Sarah, apologising for a
         2-day shipping delay, offering a 10% discount, under 120 words."
```

**Principle 6: Ask it to think step by step (for reasoning tasks).**

```
Strong: "A train travels 60 km in 1.5 hours. What is its average speed?
         Think step by step, then give the final answer."
```

### 11.3 Examples of Good Prompts

Let us put the principles together. Here are full, well-crafted prompts you could use today:

**For learning:**
```
"You are a friendly tutor. Explain how the internet works to a complete
 beginner, using an analogy involving the postal system. Keep it under
 200 words and end with a one-sentence summary."
```

**For writing:**
```
"Write a warm, professional LinkedIn post (about 100 words) announcing that
 I just completed a course on Large Language Models. Use an encouraging tone
 and include one practical thing I learned. Avoid sounding boastful."
```

**For productivity:**
```
"Here are my messy meeting notes: [paste notes]. Turn them into:
 1) a 3-bullet summary, and 2) a list of action items with owners.
 Use clear, concise language."
```

**For coding (even for beginners):**
```
"You are a helpful programming teacher. Write a short Python program that
 asks the user for their name and prints a greeting. Add a one-line comment
 explaining each line so a beginner can follow."
```

**A simple prompt checklist to remember:**

```
GOOD PROMPT CHECKLIST
  [ ] Clear task (what exactly do you want?)
  [ ] Role/persona (who should the model be?)
  [ ] Format (list? table? word count?)
  [ ] Context (background info, audience)
  [ ] Examples (if the task is tricky)
  [ ] Constraints (length, tone, what to avoid)
```

### 11.4 A Few Best Practices

- **Iterate.** Your first prompt rarely gives the perfect answer. Refine it based on what you get back.
- **Break big tasks into smaller ones.** Ask step by step rather than all at once.
- **Verify important outputs** (remember hallucinations from Chapter 10).
- **Tell it what to do, not just what to avoid.** Positive instructions work better than only negatives.
- **For consistency, lower the temperature; for creativity, raise it.**

### Chapter 11 Summary

- People use LLMs through **chat apps** (everyone) or **APIs** (developers building products).
- An API request includes a **prompt, settings (like temperature), and an API key.**
- **Prompt engineering** is the skill of asking well; it dramatically improves results.
- Core principles: be **specific**, give a **role**, specify the **format**, provide **examples**, add **context/constraints**, and ask it to **think step by step.**
- Best practices: iterate, break tasks down, verify, give positive instructions, and tune temperature.

### Reflection Questions

1. Why does giving the model a "role" often improve its answer?
2. How could providing examples (few-shot) help with a tricky classification task?
3. Which item on the prompt checklist do you think you would most often forget?

### Exercise

Take a vague request like "help me plan a trip." Rewrite it into a strong prompt using at least four items from the prompt checklist. Compare the two and note the difference.

---

## Chapter 12: Future of LLMs

You now understand how today's LLMs work. Let us close by looking ahead. The field moves incredibly fast, but a few clear trends show where things are heading.

### 12.1 Major Trends

**1. Bigger context windows.** Models can hold more and more text at once -- moving from a few pages to entire books or codebases. This means an LLM can reason over huge documents without forgetting the beginning.

**2. Smaller, more efficient models.** Alongside giant models, researchers are creating compact models that run on phones and laptops while staying surprisingly capable. This brings AI to more devices, cheaply and privately.

**3. Better reasoning.** Newer models are trained to "think" more carefully -- taking time to work through problems step by step before answering, which improves math, logic, and planning.

**4. Fewer hallucinations and more grounding.** Models are increasingly connected to real data sources so they can look up facts instead of guessing, making answers more trustworthy.

**5. Lower cost.** As techniques improve, the price per token keeps falling, making AI features affordable for more applications.

### 12.2 Multimodal Models

Early LLMs handled only text. **Multimodal models** handle multiple types of input and output -- text, images, audio, and even video -- all in one model.

**What this enables:**
- Show the model a photo and ask, "What is in this picture?"
- Speak to it and have it speak back.
- Give it a chart or screenshot and have it explain or extract the data.
- Ask it to generate an image from a text description.

**Analogy:** A text-only model is like a brilliant pen-pal who can only read and write letters. A multimodal model is like a friend who can also **see, hear, and speak** -- a much richer way to interact.

GPT-4o, Gemini, and Claude are examples of models moving strongly in this multimodal direction. The future of AI is not just about words -- it is about understanding the world through many senses at once.

### 12.3 Agent-Based Systems

Perhaps the biggest shift ahead is from LLMs that just *talk* to LLMs that *act.* These are called **AI agents.**

A regular LLM answers your question. An **agent** can take **actions** to accomplish a goal -- using tools, browsing the web, running code, sending emails, or controlling other software -- often in multiple steps, deciding what to do next on its own.

**Analogy:** A normal LLM is like a knowledgeable **advisor** who tells you what to do. An agent is like a capable **assistant** who actually goes and does it for you.

**Simple example of an agent at work:**
```
Goal: "Find me a flight to Tokyo next month under $800 and book it."

Agent steps (it decides these itself):
  1. Search flight websites (uses a web tool)
  2. Compare prices and times (reasons over results)
  3. Pick the best option under $800
  4. Fill in the booking form (uses a booking tool)
  5. Report back to you for confirmation
```

To do this safely, agents need ways to use external **tools** in a standardised manner -- which is exactly why standards like the **Model Context Protocol (MCP)** are emerging: to give agents a common, safe way to connect to tools and data sources.

**Why this matters:** Agents could automate complex, multi-step work -- research, scheduling, data analysis, customer service -- shifting LLMs from helpful chatbots to genuine digital coworkers. This is one of the most exciting and active frontiers in AI today.

### 12.4 A Balanced Look Ahead

The future is bright, but thoughtful progress matters. Important ongoing challenges include:
- **Safety and control:** making sure agents act only as intended.
- **Reducing bias and hallucinations** further.
- **Privacy:** handling personal data responsibly.
- **Fair and wise use:** ensuring these tools help people broadly.

The technology will keep improving rapidly. Understanding the fundamentals -- which you now do -- is the best foundation for using it well and following where it goes next.

### Chapter 12 Summary

- Trends: **bigger context windows, smaller efficient models, better reasoning, less hallucination, lower cost.**
- **Multimodal models** handle text, images, audio, and video together -- AI that can see, hear, and speak.
- **AI agents** go beyond talking to *acting* -- using tools to complete multi-step goals, enabled by standards like **MCP.**
- The future is promising but requires care around **safety, bias, privacy, and responsible use.**
- Your grasp of the fundamentals is the perfect base for keeping up with what comes next.

### Reflection Questions

1. What is the difference between a regular LLM and an AI agent?
2. Why are multimodal models more flexible than text-only ones?
3. Why might standards (like MCP) matter as agents become more common?

### Exercise

Imagine a helpful AI agent for your own daily life. Describe a goal you would give it and list the step-by-step actions it might take to achieve that goal.

---

## Glossary of Key Terms

**Agent (AI Agent):** An LLM-powered system that can take actions using tools to accomplish multi-step goals, not just produce text.

**API (Application Programming Interface):** A way for one program to communicate with another; developers use it to send prompts to an LLM and receive responses.

**Artificial Intelligence (AI):** The broad field of making machines perform tasks that normally require human intelligence.

**Attention:** The mechanism that lets a model decide which other words to focus on when interpreting each word.

**Bias:** Unfair assumptions a model can absorb from human-written training data.

**BERT:** An encoder-only model from Google, specialised in understanding text (e.g., for search).

**Context:** All the text the model is currently looking at to make its prediction.

**Context Window:** The maximum number of tokens a model can consider at once -- its short-term memory.

**Decoder:** The part of a Transformer that generates text one token at a time, looking only at past tokens.

**Deep Learning:** A type of machine learning using neural networks with many layers.

**Embedding:** A list of numbers (a vector) representing a token's meaning, so that similar meanings have similar numbers.

**Encoder:** The part of a Transformer that reads and understands input text, looking in both directions.

**Fine-tuning:** A second training stage that shapes a pretrained model's behaviour for a specific purpose.

**GPT (Generative Pre-trained Transformer):** A decoder-only model family from OpenAI; the basis of ChatGPT.

**Hallucination:** When a model confidently produces information that is false.

**Knowledge Cutoff:** The point in time after which a model has no built-in knowledge of events.

**LLaMA:** An open-weight, decoder-only model family from Meta.

**Large Language Model (LLM):** A deep learning model trained on massive text to understand and generate human language.

**Machine Learning (ML):** Achieving AI by learning patterns from examples rather than from hand-written rules.

**Multimodal:** A model that can handle multiple input/output types -- text, images, audio, video.

**N-gram:** An early language model approach based on counting groups of n words.

**Neural Network:** A web of connected units ("neurons") in layers, loosely inspired by the brain.

**Parameter:** One of the model's adjustable internal "knobs" tuned during training; modern LLMs have billions.

**Pretraining:** The first, large-scale training stage where a model learns general language by predicting the next token.

**Prompt:** The input text/instructions you give to an LLM.

**Prompt Engineering:** The skill of writing effective prompts to get better results.

**Query, Key, Value (Q, K, V):** The three roles inside attention -- Queries search, Keys advertise relevance, Values deliver information.

**RLHF (Reinforcement Learning with Human Feedback):** Training a model to prefer answers that humans rate as good.

**Self-Attention:** Attention applied within a single text, where every word examines every other word.

**Supervised Learning:** Learning from labelled examples (input paired with correct output).

**T5 (Text-To-Text Transfer Transformer):** An encoder-decoder model from Google that frames every task as text-to-text.

**Temperature:** A setting controlling randomness/creativity in generation; low = focused, high = creative.

**Token:** A small chunk of text (word, sub-word, or character) -- the basic unit an LLM processes.

**Tokenization:** The process of splitting text into tokens.

**Top-k:** A sampling setting that limits choices to the k most likely tokens.

**Top-p (Nucleus Sampling):** A sampling setting that limits choices to the top tokens whose probabilities sum to p.

**Transformer:** The neural network architecture (introduced in 2017) behind all modern LLMs, built on self-attention.

**Unsupervised Learning:** Learning from raw, unlabelled data (such as next-token prediction on plain text).

**Vector:** A list of numbers; embeddings are vectors that represent meaning.

---

## Final Words

Congratulations -- you have travelled from "What is AI?" all the way to AI agents and the future of the field.

Let us recall the single thread that ties it all together: **an LLM is a system that predicts the next token, trained on massive text, powered by the Transformer and its attention mechanism.** Everything else -- embeddings, context windows, training stages, temperature, hallucinations, prompting -- builds on that one simple core idea.

You can now confidently explain:
- How AI, ML, Deep Learning, and LLMs relate.
- What tokens and embeddings are.
- How attention and Transformers work.
- The difference between encoder, decoder, and encoder-decoder models.
- How models are trained (pretraining, fine-tuning, RLHF).
- How text is generated and controlled (temperature, top-k, top-p).
- What LLMs are good at, and where they fail.
- How to write strong prompts.
- Where the field is heading.

The best next step is to **practise.** Open a chat assistant, try the prompting techniques from Chapter 11, experiment, and observe. The fundamentals you have learned here will make everything you see click into place.

Welcome to the world of Large Language Models. You are no longer a complete beginner.