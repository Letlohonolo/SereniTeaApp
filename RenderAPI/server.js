const express = require("express");
const cors = require("cors");
const admin = require("firebase-admin");
const app = express();

app.use(cors());
app.use(express.json());

const serviceAccount = JSON.parse(process.env.FIREBASE_CONFIG);

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
    res.status(500).json({ success: false, error: err.message });
  }
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => console.log(`Server running on port ${PORT}`));
