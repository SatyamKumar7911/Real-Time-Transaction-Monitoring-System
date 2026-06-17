-- Schema update for enhanced logs table with transaction tracking
-- Add new columns to existing logs table for Kafka transaction logging

ALTER TABLE logs ADD COLUMN IF NOT EXISTS transaction_id VARCHAR(255);
ALTER TABLE logs ADD COLUMN IF NOT EXISTS transaction_type VARCHAR(50);
ALTER TABLE logs ADD COLUMN IF NOT EXISTS from_account VARCHAR(255);
ALTER TABLE logs ADD COLUMN IF NOT EXISTS to_account VARCHAR(255);
ALTER TABLE logs ADD COLUMN IF NOT EXISTS transaction_status VARCHAR(50);
ALTER TABLE logs ADD COLUMN IF NOT EXISTS event_source VARCHAR(50) DEFAULT 'DIRECT';
ALTER TABLE logs ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE logs MODIFY COLUMN description VARCHAR(500);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_logs_transaction_id ON logs(transaction_id);
CREATE INDEX IF NOT EXISTS idx_logs_transaction_status ON logs(transaction_status);
CREATE INDEX IF NOT EXISTS idx_logs_event_source ON logs(event_source);
CREATE INDEX IF NOT EXISTS idx_logs_created_at ON logs(created_at);
CREATE INDEX IF NOT EXISTS idx_logs_account_created_at ON logs(account_id, created_at DESC);

-- Comments for documentation
ALTER TABLE logs COMMENT = 'Enhanced logs table for tracking account transactions and Kafka events';