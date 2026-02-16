# Help & Support Module - Quick Start Guide

## 🚀 Getting Started in 5 Minutes

### Prerequisites
- ✅ Backend running on `http://localhost:8080`
- ✅ Frontend running on `http://localhost:3000`
- ✅ Logged in to the application

---

## 🎯 For Pet Owners & Veterinary Doctors

### Access Help & Support
**URL**: `http://localhost:3000/pet-owner/help` (or `/doctor/help`)

**What you'll see:**
- Header with "Help & Support" title and "Raise New Issue" button
- List of your existing support tickets
- Filters: All, Open, In Progress, Resolved

### Create a Support Ticket

1. Click the **"Raise New Issue"** button
2. Select a **Category**:
   - 📅 Appointment Issue
   - 💳 Payment Issue
   - 📦 Order Issue
   - 👤 Account Issue
   - ⚙️ Technical Issue
   - ❓ Other
3. Enter **Subject** (max 100 characters)
4. Enter **Description** (max 1000 characters)
5. Click **"Create Ticket"**
6. **Email notification** will be sent to your inbox
7. You'll be redirected to ticket list

### Chat with Admin

1. **Click a ticket** in the list
2. **ChatWindow opens** showing:
   - Message history (if any)
   - Your messages on the left (purple)
   - Admin messages on the right (gray)
   - Timestamps on each message
3. **Type your message** in the text area
4. Click **"Send Message"**
5. Message appears immediately in chat
6. **Admin will reply** within hours
7. You'll receive **email notification** when admin replies
8. Messages **auto-update every 2 seconds** (no refresh needed)

### Track Ticket Status

**Status Meanings:**
- 🟡 **OPEN** - Just created, waiting for admin
- 🔵 **IN_PROGRESS** - Admin is working on it
- 🟢 **RESOLVED** - Issue resolved, ticket closing
- ⚫ **CLOSED** - Ticket closed, can't add messages

**When status is RESOLVED/CLOSED:**
- ✉️ You'll receive a **closure email**
- 📝 You can **view chat history** but can't send new messages
- 🔄 You can create a **new ticket** if needed

---

## 👨‍💼 For Admin Support Staff

### Access Admin Dashboard
**URL**: `http://localhost:3000/admin/support`

**What you'll see:**
- Three tabs showing ticket counts:
  - **New Tickets** (OPEN status) - New issues to handle
  - **Active** (IN_PROGRESS status) - Issues being worked on
  - **Resolved** (RESOLVED status) - Closed issues

### Manage Tickets

#### View New Tickets
1. Click **"New Tickets"** tab
2. See count of open tickets
3. Browse tickets in card format showing:
   - Ticket ID and subject
   - Category and user role
   - Description preview
   - Message count
   - Creation date

#### Open a Ticket
1. **Click a ticket card**
2. **AdminTicketPanel opens** showing:

**Left Side - Ticket Details:**
- Ticket ID and subject
- User name and role
- Category
- Full description
- Creation and update dates
- **Status dropdown** (Open, In Progress, Resolved, Closed)

**Right Side - Chat Section:**
- Full chat history with user
- Your messages (admin) in purple
- User messages in blue
- Timestamps
- Message reply form

### Respond to Customer

1. Scroll to the **message reply form** at bottom
2. Type your **reply message** (max 1000 chars)
3. Click **"Send Reply"**
4. Your message appears in chat immediately
5. **Email sent to user** with your reply
6. Ticket **auto-updates to IN_PROGRESS**

### Update Ticket Status

1. In the **Status dropdown** (left side)
2. Select new status:
   - `OPEN` - Initial state
   - `IN_PROGRESS` - Working on it (auto-set when you reply)
   - `RESOLVED` - Issue resolved
   - `CLOSED` - Ticket closed

3. Status changes **immediately**
4. Ticket moves to **appropriate tab**
5. **Email sent to user** (if status is RESOLVED/CLOSED)

### Dashboard Auto-Refresh

- Dashboard **auto-refreshes every 5 seconds**
- New tickets appear automatically
- Ticket counts update in real-time
- No manual refresh needed

---

## 📧 Email Examples

### Example 1: Ticket Creation Email
```
Subject: Support Ticket Created - Ticket #1

Hello John,

Your support ticket has been created successfully!

Ticket ID: #1
Subject: Appointment booking issue
Category: Appointment
Status: Open

We will review your issue and get back to you shortly.

Thank you for contacting Smart Pet Care!
```

### Example 2: Admin Reply Email
```
Subject: Re: Your Support Ticket #1

Hello John,

Our support team has replied to your ticket:

Admin: "I understand your concern. Let me help you book the appointment. 
Can you please provide your preferred date and time?"

View your ticket: [Link]

Thank you for choosing Smart Pet Care!
```

### Example 3: Ticket Resolved Email
```
Subject: Your Support Ticket #1 is Resolved

Hello John,

Your support ticket has been resolved!

Ticket ID: #1
Subject: Appointment booking issue
Status: Resolved

Thank you for your patience. If you have any further questions, 
feel free to create a new ticket.

Smart Pet Care Support Team
```

---

## 🔄 Real-Time Chat Flow

### How Polling Works
- Frontend **polls every 2 seconds** for updates
- No WebSocket or long-polling overhead
- Chat feels real-time to user
- Messages appear within 2 seconds

### Example Timeline
```
10:00:00 - User sends message
          └─ Message appears immediately (optimistic update)
          └─ Request sent to server

10:00:01 - Admin sees message on dashboard (5-sec refresh)
          
10:00:02 - User polls for new messages
          └─ No new messages yet

10:00:03 - Admin replies
          └─ Admin's message posted to server
          └─ Admin sees reply in chat immediately

10:00:05 - User polls for new messages
          └─ Admin's reply appears in chat
          └─ User sees reply instantly (2-sec polling)
          └─ Email notification queued (async)

10:00:10 - Email sent to user's inbox
```

---

## ❓ Common Questions

### Q: How long does it take to get a response?
**A**: Response time depends on admin availability. Once admin replies, you'll see it in chat within 2 seconds and receive an email notification immediately.

### Q: Can I edit my message after sending?
**A**: No, messages are immutable. Create a new message if you need to correct something.

### Q: What if I need to follow up later?
**A**: Keep your ticket open by not changing the status. You can send messages anytime until it's resolved.

### Q: Can admin close my ticket without resolving it?
**A**: Yes, but they should use CLOSED status only if the issue needs no further action. RESOLVED status is for completed issues.

### Q: Do I get notified for every message?
**A**: Yes! You receive an email notification for:
- Ticket creation
- Each admin reply
- Ticket status change to RESOLVED/CLOSED

### Q: Can I see tickets from previous sessions?
**A**: Yes! Click the "All" filter to see all your tickets including old ones. Filter by status to narrow down.

### Q: What is the character limit for messages?
**A**: 
- Subject: 100 characters
- Description: 1000 characters
- Chat messages: 1000 characters

---

## 🛠️ Troubleshooting

### Problem: Can't see Help & Support link
**Solution**: 
- Ensure you're logged in
- Go directly to `/pet-owner/help` or `/doctor/help` or `/admin/support`
- Check browser address bar

### Problem: "Ticket creation failed"
**Solution**:
- All fields required (category, subject, description)
- Check internet connection
- Try again or refresh page
- Check browser console for errors (F12)

### Problem: Messages not updating in chat
**Solution**:
- Check internet connection
- Refresh the page
- Polling runs every 2 seconds, wait if needed
- Check backend logs for errors

### Problem: Email not received
**Solution**:
- Check spam/junk folder
- Verify email address in profile
- Try creating another ticket
- Email may take up to 5 minutes

### Problem: Admin panel shows no tickets
**Solution**:
- Ensure logged in as ADMIN
- Try navigating directly to `/admin/support`
- Refresh the page
- Check if any PET_OWNER has created tickets

---

## 📊 Status Flow Diagram

```
User Creates Ticket
         ↓
    [OPEN]  ← Status: Open (Initial)
         ↓
   Email sent to user
         ↓
  Admin views ticket
         ↓
   Admin replies
         ↓
  Status auto-updates
         ↓
    [IN_PROGRESS] ← Status: In Progress
         ↓
   Email sent to user
         ↓
  User can reply anytime
         ↓
   Admin changes status
         ↓
  [RESOLVED] ← Status: Resolved (Issue fixed)
         ↓
   Email sent to user
         ↓
   User can't send messages
  (can view chat only)
         ↓
   [CLOSED] ← Status: Closed (Optional)
         ↓
   Ticket archived
```

---

## 🎓 Example Scenarios

### Scenario 1: Booking Issue (Pet Owner)
```
1. Pet Owner: "I can't book appointment for my dog"
2. System: Email sent with ticket confirmation
3. Admin (5 min later): "What date were you trying?"
4. System: Email sent to user, ticket marked IN_PROGRESS
5. User: "I need it on Feb 15 at 2 PM"
6. Admin: "I've booked it for you. Check your calendar."
7. System: Email sent
8. Admin: Changes status to RESOLVED
9. System: Email sent confirming resolution
10. User: Can view chat but can't reply
```

### Scenario 2: Payment Issue (Vet Doctor)
```
1. Doctor: "I didn't receive my payment for Feb"
2. System: Email sent
3. Admin (1 hour): "Let me check with finance team"
4. System: Ticket IN_PROGRESS, email sent
5. Admin (2 hours): "Payment processed. Should arrive in 1-2 days"
6. System: Email sent
7. Admin: Changes to RESOLVED
8. System: Email confirms
9. Doctor: Can see resolved status
```

---

## 📞 Support Contact

For issues with the Help & Support module itself:
- Email: support@smartpetcare.com
- Create a ticket with category "TECHNICAL"

---

## 🎉 You're Ready!

The Help & Support module is ready to use. Start by:

1. **User**: Go to `/pet-owner/help` → Create a ticket
2. **Admin**: Go to `/admin/support` → Manage tickets
3. **Chat**: Send messages back and forth
4. **Email**: Receive notifications
5. **Status**: Track progress

Enjoy seamless support communication! 🐾
