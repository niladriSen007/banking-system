# Transaction
curl.exe -X POST `
  -H "Content-Type: application/json" `
--data-binary "@transaction-outbox-connector.json" `
http://localhost:8083/connectors



# Fraud Detection
curl.exe -X POST `
  -H "Content-Type: application/json" `
--data-binary "@fraud-detection-outbox-connector.json" `
http://localhost:8083/connectors