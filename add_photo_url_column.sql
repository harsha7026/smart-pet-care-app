-- Add photo_url column to pets table if it doesn't exist
-- This migration is for Phase 3: Pet Photo Upload feature

ALTER TABLE pets ADD COLUMN photo_url VARCHAR(500) AFTER medical_notes;

-- Create uploads directory in application for storing pet photos
-- The application will create this automatically when first photo is uploaded
-- Location: uploads/pet-photos/
