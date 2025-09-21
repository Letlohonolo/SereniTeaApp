const express = require("express");
const cors = require("cors");
const admin = require("firebase-admin");
const app = express();
const bodyParser = require("body-parser");

app.use(cors());
app.use(express.json());
app.use(bodyParser.json());

let serviceAccount;
try {
  serviceAccount = JSON.parse(process.env.FIREBASE_CONFIG);
} catch (err) {
  console.error("Failed to parse FIREBASE_CONFIG:", err.message);
  process.exit(1);
}

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
});

const db = admin.firestore();

// Test endpoint
app.get("/hello", (req, res) => {
  res.json({ message: "Hello from Render API!" });
});

// Save data to Firebase
app.post("/addEntry", async (req, res) => {
  try {
    const { remoteId, title, content, dateIso, attachmentUri, createdAt } =
      req.body;

    if (!title || !content) {
      return res
        .status(400)
        .json({ success: false, error: "Title and content are required." });
    }

    const docRef = await db.collection("journal").add({
      remoteId,
      title,
      content,
      dateIso,
      attachmentUri,
      createdAt: createdAt || Date.now(),
    });

    res.json({ success: true, id: docRef.id });
  } catch (err) {
    console.error("Error adding entry:", err);
    res.status(500).json({ success: false, error: err.message });
  }
});

app.get("/entries", async (req, res) => {
  try {
    const snapshot = await db
      .collection("journal")
      .orderBy("createdAt", "desc")
      .get();
    const entries = snapshot.docs.map((doc) => ({
      id: doc.id,
      ...doc.data(),
    }));
    res.json({ success: true, entries });
  } catch (err) {
    console.error("Error fetching entries:", err);
    res.status(500).json({ success: false, error: err.message });
  }
});

let cachedQuote = null;
let lastDate = null;

app.get("/daily-quote", async (req, res) => {
  try {
    const today = new Date().toISOString().split("T")[0]; // YYYY-MM-DD

    // Return cached quote if it's still valid
    if (cachedQuote && lastDate === today) {
      return res.json(cachedQuote);
    }

    // Fetch quotes from Firestore
    const snapshot = await db.collection("quotes").get();
    const quotes = snapshot.docs.map((doc) => doc.data());

    if (quotes.length === 0) {
      return res.status(404).json({ error: "No quotes found" });
    }

    // Pick a random quote
    const randomQuote = quotes[Math.floor(Math.random() * quotes.length)];

    // Cache it
    cachedQuote = randomQuote;
    lastDate = today;

    res.json(randomQuote);
  } catch (err) {
    console.error("Error fetching quote:", err);
    res.status(500).json({ error: "Internal server error" });
  }
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => console.log(`Server running on port ${PORT}`));
