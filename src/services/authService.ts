import jwt from "jsonwebtoken";
import bcrypt from "bcryptjs";
import * as bcryptModule from "bcryptjs";
import { pool } from "../config/database";

const JWT_SECRET = process.env.JWT_SECRET || "cochiwawa_secret";

export interface User {
    id: string;
    email: string;
    role: string;
    fullName?: string;
    gender?: string;
    nationalId?: string;
    profilePhotoUrl?: string;
}

export async function registerUser(
    email: string,
    password: string,
    role: string,
    fullName: string,
    gender: string,
    nationalId: string,
    profilePhotoUrl: string
): Promise<User> {
    const passwordHash = await bcrypt.hash(password, 10);
    const result = await pool.query(
        "INSERT INTO users (email, password_hash, role, full_name, gender, national_id, profile_photo_url) VALUES ($1, $2, $3, $4, $5, $6, $7) RETURNING id, email, role, full_name as \"fullName\", gender, national_id as \"nationalId\", profile_photo_url as \"profilePhotoUrl\"",
        [email, passwordHash, role, fullName, gender, nationalId, profilePhotoUrl]
    );
    return result.rows[0];
}

export async function loginUser(email: string, password: string): Promise<{user: User, token: string} | null> {
    const result = await pool.query("SELECT id, email, password_hash, role, full_name as \"fullName\", gender, national_id as \"nationalId\", profile_photo_url as \"profilePhotoUrl\" FROM users WHERE email = $1", [email]);
    if (result.rows.length === 0) return null;

    const user = result.rows[0];
    const isPasswordValid = await bcrypt.compare(password, user.password_hash);
    if (!isPasswordValid) return null;

    const token = generateJWT(user.id, user.role);
    return {
        user: {
            id: user.id,
            email: user.email,
            role: user.role,
            fullName: user.fullName,
            gender: user.gender,
            nationalId: user.nationalId,
            profilePhotoUrl: user.profilePhotoUrl
        },
        token
    };
}

export function generateJWT(userId: string, role: string) {
    return jwt.sign(
        { userId, role },
        JWT_SECRET,
        { expiresIn: "7d" }
    );
}

export function verifyJWT(token: string) {
    try {
        return jwt.verify(token, JWT_SECRET);
    } catch (e) {
        return null;
    }
}
