# Karbovanets

A desktop app for personal finance tracking. Record your income and expenses
by category and see where your money goes.

<img width="1836" height="565" alt="image" src="https://github.com/user-attachments/assets/833a5de3-4f35-4d0f-a8c8-e5737ba1abba" />

This is the main window of the program, where user can filter all the transactions by date range, analize pie charts and list of transactions.

<img width="646" height="403" alt="image" src="https://github.com/user-attachments/assets/afe37e53-c669-4e17-a4bf-301b197bc2b1" />

Here is another program's window, which opens by clicking button "Add Transaction". User can add various transactions and new categories here.

## Features
- Add income and expense transactions with a category and date
- Filter transactions by a custom date range
- Pie charts for income and expenses
- Data is saved between launches (plain text files in `cache/`)

## Tech Stack
Java 25, Swing, Maven, [XChart](https://github.com/knowm/XChart)

## Project Structure
- `Karbovanets.java` - main window, data loading, charts
- `AddTransaction.java` - window for adding new transactions
- `Transaction.java`, `Category.java` - data models
- `cache/` - saved categories and transactions

## Roadmap
- Unit tests for balance calculation
- Add currencies
