<div align="center">
  
  # 🛒 E-Commerce CLI System
  
  **A lightweight, object-oriented E-Commerce backend built purely in Java.**
  
  ![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
  ![CLI](https://img.shields.io/badge/Interface-CLI-4CAF50?style=for-the-badge)
  ![Data](https://img.shields.io/badge/Database-CSV-blue?style=for-the-badge)

</div>

<br/>

This application simulates a lightning-fast shopping cart experience entirely via the command-line interface. It seamlessly reads product catalogs and coupon codes from external CSV files, allowing users to interactively add items to their cart, apply dynamic discounts, and generate a final itemized receipt in Indian Rupees (INR).

---

## ✨ Key Features

- 📦 **Massive Product Catalog:** Seamlessly loads **1,000 unique, realistic products** (spanning 10 real-world categories) directly from an external CSV file.
- 🧠 **Smart User Experience (UX):** Intelligent auto-padding allows users to enter quick numbers (e.g., typing `1` instantly resolves to `P001`). 
- 🎟️ **Numeric Coupon Selection:** Select coupons via a numbered numeric list, completely eliminating typing errors.
- 🔄 **Interactive CLI Checkout:** Browse items, manage your cart, remove items dynamically, and apply percentage-based coupon codes on the fly.
- 🧾 **Persistent Receipt Generation:** Calculates subtotals and discounts, outputs a neatly formatted `"THANK YOU :)"` receipt to the console, and simultaneously auto-exports it to `output/sample_outputs.txt`.

---

## 📂 Project Structure

```text
Assignment_SangsaptakBanerjee/
├── 📁 src/                # Core Java source files (Models, Cart logic, Utils)
├── 📁 data/               # CSV database files (1,000 products & coupons)
├── 📁 output/             # Generated checkout receipts
└── 📁 docs/               # Architecture, bug, and test reports
```

---

## 🚀 How to Run

### 🛠️ Prerequisites
- **Java Development Kit (JDK) 8** or higher installed on your machine.

### ⚙️ Compilation
Open your terminal, navigate to the root directory of the project, and compile the source code:
```bash
javac src/*.java
```

### ▶️ Execution
Run the compiled `main` class:
```bash
java -cp src main
```

> ⚠️ **Important Note:** Ensure your terminal's current working directory is the root of the project so the application can correctly locate the `data/` and `output/` folders!

---

## 🕹️ Usage

When you launch the program, you will be greeted with our massive catalog of products. 
Simply follow the clean on-screen prompts to:
1. Input **Product IDs** (e.g., simply type `1`).
2. Specify your desired **quantities**.
3. Type a **coupon's serial number** to snag a discount before checking out!
