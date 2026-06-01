import {pool} from "../config/database";
import fs from "fs";
import path from "path";

async function runMigrations() {
    try{
        const migrationsDir = path.join(__dirname);
        const files = fs.readdirSync(migrationsDir)
        .filter(file => file.endsWith(".sql"))
        .sort();// ensures 001 -> 005

        for(const file of files){
            console.log(`Running migration: ${file}`);
            const sql = fs.readFileSync(path.join(migrationsDir, file), "utf-8");
            await pool.query(sql);
        }
            console.log(`All migrations ran successfully.`);
            process.exit(0);
    }catch(error){
        console.error("Error running migrations:", error);
        process.exit(1);
        }

        // Run the migrations
        runMigrations();
    }