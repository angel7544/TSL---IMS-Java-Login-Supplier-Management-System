
# Inventory Management System

A full-featured Java application for managing inventory, products, and suppliers.

## Overview

This Inventory Management System helps businesses track products, manage suppliers, monitor stock levels, and generate reports. Built with Java Swing, it provides an intuitive graphical interface for all inventory management operations.

## Features

- **User Authentication**: Secure login system with role-based access
- **Product Management**: Add, edit, delete, sell, and restock products
- **Supplier Management**: Track supplier information and relationships
- **Inventory Tracking**: Real-time monitoring of stock levels
- **Category Filtering**: Organize products by categories
- **Low Stock Alerts**: Automatic notifications for items running low
- **Data Import/Export**: CSV import/export functionality
- **Reporting**: Generate inventory reports and analytics
- **Backup & Restore**: Data backup and restoration capabilities

## Technical Details

- **Language**: Java (requires Java 14 or higher)
- **UI Framework**: Java Swing
- **Architecture**: MVC pattern
- **Build Tool**: Maven
- **Dependencies**:
  - Apache POI (Excel support)
  - iText (PDF support)
  - JUnit (Testing)

## Getting Started

### Prerequisites

- Java Development Kit (JDK) 14 or higher
- Maven 3.6 or higher

### Installation

1. Clone the repository
2. Navigate to the project directory
3. Build the project:
   ```
   mvn clean package
   ```
4. Run the application:
   ```
   java -cp target/myartifactid-0.0-SNAPSHOT-jar-with-dependencies.jar Main
   ```

### Login Credentials

- **Default Username**: admin
- **Default Password**: admin

## Usage Guide

### Main Dashboard

The main dashboard displays all products in a sortable table with the following features:
- Sort by clicking column headers
- Filter by category
- Search by product name

### Product Operations

- **Add Product**: Complete the form in the left panel and click "Add"
- **Edit Product**: Select a product, modify values, and click "Update"
- **Delete Product**: Select a product and click "Delete"
- **Sell Product**: Select a product and click "Sell" to reduce inventory
- **Restock Product**: Select a product and click "Restock" to add inventory

### Supplier Management

Access the supplier management dialog to:
- Add new suppliers
- Edit existing supplier information
- Delete suppliers
- Associate suppliers with products

### Reports & Utilities

- Generate inventory reports
- Export data to CSV
- Backup database
- Restore from backup

## System Requirements

- Operating System: Windows, macOS, or Linux
- Minimum RAM: 2GB
- Disk Space: 100MB

## Development

This project follows standard Java development practices:
- Each class has a single responsibility
- DAO pattern for data access
- MVC pattern for UI separation

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- Icons from [Icon Library]
- UI design inspiration from [Design Source]
