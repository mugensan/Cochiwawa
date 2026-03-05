import express, { Express } from "express";
import dotenv from "dotenv";

dotenv.config();

const app: Express = express();
const PORT = process.env.PORT || 5000;

app.get("/", (req, res) => {
    res.send("Hello Cochiwawa backend");
});

app.listen(PORT, () => {
    console.log(`Cochiwawa backend is running on http://localhost:${PORT}`);
});

