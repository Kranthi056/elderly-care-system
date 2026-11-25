# ElderCare — Elderly Care Management System

A complete Spring Boot + Thymeleaf platform that connects elderly individuals and families with verified caregivers, with full admin controls for requests, assignments, and monitoring.

## Tagline
**Compassionate Care for a Better Tomorrow** — Organize care. Stay informed. Feel at ease.

## Features

### Public website
- Home with hero, services, how-it-works, benefits, testimonials, CTAs
- About (mission, vision, objectives)
- Services catalog
- How It Works
- Contact + FAQ

### Authentication
- Login with demo credentials
- Separate registration for Elderly/Family and Caregiver

### Elderly / Family dashboard
- Dashboard overview (requests, assignments, alerts)
- Profile & medical information
- Browse services & request care
- Track request status (Pending → Approved → Assigned → …)
- View assigned caregiver
- Care schedule & progress reports
- Messages (demo conversation UI)
- Notifications
- Feedback & ratings

### Caregiver dashboard
- Verification status
- Accept / reject / start assignments
- View elderly details (authorized medical info)
- Schedule
- Submit care progress reports
- Notifications

### Admin dashboard
- Live stats (users, caregivers, pending approvals, requests, assignments)
- Manage elderly users & caregivers
- Approve / reject caregiver applications
- Manage care services
- Approve requests & assign caregivers
- Monitor care reports

## Tech Stack
- Java 17 · Spring Boot 3.2 · Spring Security · Spring Data JPA + Hibernate
- **H2 in-memory database** (no external MySQL required)
- Thymeleaf · Custom modern CSS (soft teal + cream, 3D card effects)

## Default Credentials

| Role | Email | Password |
|------|-------|----------|
| Admin | admin@elderlycare.com | admin123 |
| Elderly | margaret@example.com | elderly123 |
| Elderly | james@example.com | elderly123 |
| Caregiver | sarah.care@example.com | caregiver123 |
| Caregiver | david.care@example.com | caregiver123 |
| Pending | pending.care@example.com | caregiver123 |

## How to Run

```bash
cd elderly-care-system
mvn spring-boot:run
```

Open: **http://localhost:8080**

H2 Console (optional): http://localhost:8080/h2-console  
JDBC URL: `jdbc:h2:mem:elderlycare` · User: `sa` · Password: (empty)

## Design Notes
- Soft teal primary (`#2A9D8F`), cream backgrounds — no pure black or blue page backgrounds
- Large readable fonts, clear status badges, accessible forms
- Subtle 3D hover on cards, soft shadows, consistent rounded UI
- Role-based sidebars with grouped navigation

Sample data (users, 6 services, requests, assignment, notifications) is seeded on first startup.
