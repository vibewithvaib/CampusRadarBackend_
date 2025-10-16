# 🚀 CampusRadar - Backend

![Spring Boot](https://img.shields.io/badge/SpringBoot-3.0-brightgreen?style=for-the-badge&logo=springboot)
![MySQL](https://img.shields.io/badge/Database-MySQL-orange?style=for-the-badge&logo=mysql)
![JWT](https://img.shields.io/badge/Security-JWT-yellow?style=for-the-badge&logo=jsonwebtokens)
![GenAI](https://img.shields.io/badge/GenAI-Integration-purple?style=for-the-badge&logo=openai)

---

## 🎯 The Problem

Traditional internship platforms often feel **disconnected and there is no bridge to connect for many campus and industry for internship opportunities**:

- 🎓 Students struggle to find opportunities that truly match their skills.  
- 🧑‍💼 Recruiters are overwhelmed with applications, making it hard to find the right candidates.  
- 🏛️ College Admins have no oversight, making it difficult to track student participation or verify internships for academic credit (a key requirement under **NEP 2020**).  

**CampusRadar bridges these gaps by creating a single, intelligent ecosystem.**

---

## ✨ Core Features

The backend provides a full suite of **role-based APIs**.

### 🧑‍🎓 Student Features
- Secure registration and profile management  
- Create/update detailed profiles (skills, headline, resume)  
- Apply for internships and track application status in real-time  

### 🏢 Recruiter Features
- Secure registration and profile management  
- Post, update, and manage internship listings  
- View all applicants for specific internships  
- Update applicant status (`SHORTLISTED`, `REJECTED`, `HIRED`)  
  - Triggers **automatic email notifications**  

### 🔒 Admin Features
- Secure admin dashboard for platform-wide management  
- Fetch pending user/internship approvals  
- Approve or reject accounts and postings for quality control  

---

## 🤖 GenAI Integration

### 🧠 AI-Assisted Shortlisting
A dedicated endpoint (`/ai-shortlist`) allows recruiters to automatically **shortlist best-fit candidates** using LLM analysis.

### 🎯 Personalized Recommendations
Endpoints for students and recruiters to get **intelligent, real-time recommendations** powered by AI.

---

## 🛠️ Tech Stack

| Component | Technology |
|------------|-------------|
| **Framework** | Spring Boot 3 |
| **Security** | Spring Security + JWT |
| **Database** | MySQL + Spring Data JPA (Hibernate) |
| **API Type** | REST APIs with DTOs |
| **Email** | Spring Boot Mail (Async Notifications) |
| **AI Integration** | WebClient to communicate with Python AI microservice |

---

## 🔌 API Endpoints

All endpoints are **secured and role-prefixed** under `/api`.

| Role | Endpoint Prefix | Description |
|------|------------------|-------------|
| Public | `/auth/**` | Registration & Login |
| Student | `/student/**` | Manage profile, applications |
| Recruiter | `/recruiter/**` | Manage internships & applicants |
| Admin | `/admin/**` | Approvals, dashboard management |
| AI | `/ai/**` | AI shortlisting & recommendations |

📘 **Swagger UI:** Access live documentation at  
`http://localhost:8081/swagger-ui.html`

---

## ⚙️ Local Setup & Installation

### 🧩 Prerequisites
- Java 17 or newer  
- Maven 3.8+  
- Local MySQL server  
- Python AI service running at `localhost:5001`

---



### Application Configuration

# --- DATABASE ---
spring.datasource.url=jdbc:mysql://localhost:3306/campusradar_db
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password


# --- JWT ---
jwt.secret=your_super_secret_base64_encoded_key_here

# --- EMAIL (for notifications) ---
spring.mail.username=youremail@gmail.com
spring.mail.password=your_google_app_password
⚠️ Use a Google App Password instead of your regular Gmail password.

```sql
CREATE DATABASE campusradar_db;
