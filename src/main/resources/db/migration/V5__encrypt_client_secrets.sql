-- Encrypt client secrets with BCrypt
-- Note: This migration extends the clientSecret column length to accommodate BCrypt hashes (up to 60 chars)
-- The actual encryption is handled by the Java application migration (V5_1__EncryptClientSecrets.java)

-- Extend clientSecret column to accommodate BCrypt hashes
ALTER TABLE clients ALTER COLUMN client_secret TYPE VARCHAR(255);

-- Add a flag to track which clients have encrypted secrets
-- This helps identify which secrets need migration
ALTER TABLE clients ADD COLUMN IF NOT EXISTS secret_encrypted BOOLEAN DEFAULT false;

-- Log: "Client secrets will be encrypted on next application startup"
