# Platform for Managing and Exporting Digital Goods

## Project Description
The goal of this project is to create a platform for managing digital goods such as prints and videos, with the capability to export them to various marketplaces like **Etsy** and **Redbubble**. The application should support uploading multiple images and videos for each product, storing and managing them, as well as integrating with external service APIs. All functionality is tied to individual users, allowing each user to have their own product database and settings.

## Main Tasks

### 1. User Management (Initial Phase)

**Registration and Authentication:**
- Implement a registration system for new users with data validation.
- Integrate an authentication mechanism for users using **Spring Security**.

**Profile Management:**
- Implement the ability for users to edit their profile (e.g., change password, contact information).

**Session Management:**
- Ensure secure session management with the ability to log out.

### 2. Creating a User-Specific Product Database

**Linking Products to Users:**
- Design the database structure so that each product is linked to a specific user.

**Product Tables:**
- Develop tables to store product information, including:
  - Name
  - Description
  - Category
  - Tags
  - Up to 10 images
  - One video

### 3. Uploading and Managing Images and Videos (After Users)

**Media Upload:**
- Implement functionality for uploading multiple images and one video for each product, linked to the user.
- Store images and videos locally on the computer, organized by user.

**Media Management:**
- Develop an interface for managing uploaded images and videos, including the ability to:
  - Edit metadata
  - Delete unnecessary files
  - Add new ones
  - Manage files specific to each user

### 4. Product Categorization

**Product Categories:**
- Create a categorization system with the ability for each user to create and manage categories.
- Ensure the ability to filter products by categories.

### 5. Integration with External Services (Export)

**Product Export:**
- Develop stubs for integrating with APIs of marketplaces like **Etsy**, **Redbubble**, and others.
- Implement the functionality to export products with images and videos to external platforms, using individual user credentials.

### 6. Media Storage

**Local Storage:**
- Organize the storage of images and videos locally on the computer, segregating data by user.

**Preparation for Server Migration:**
- Prepare the infrastructure for potential migration of media storage to a server in the future (e.g., **Amazon S3**).
