# PetCare AI Chatbot Implementation Guide

## Overview

A modern, AI-powered floating chatbot has been integrated into the PetCare Landing Page. The chatbot uses OpenAI's GPT API to provide intelligent, context-aware responses to help new users understand and navigate the platform.

## Features

### ✨ Chatbot Functionality
- **Floating Widget**: Non-intrusive chat bubble positioned at bottom-right
- **Smart Responses**: AI-powered using OpenAI GPT-3.5-turbo
- **Conversation History**: Maintains context across multiple messages
- **Loading States**: Visual feedback while waiting for AI response
- **Error Handling**: Graceful error messages if something goes wrong
- **Responsive Design**: Works seamlessly on desktop and mobile

### 🎯 What the Chatbot Can Help With
1. **Account Registration Guidance**: Step-by-step help creating accounts
2. **User Role Explanation**: Difference between PET_OWNER and VETERINARY_DOCTOR
3. **App Features**: Explanation of pet management, appointments, health records
4. **Veterinarian Information**: Details about:
   - Dr. Arun (General Veterinary Practice)
   - Dr. Meena (Surgical Specialist)
   - Dr. Ravi (Dermatology Specialist)
5. **FAQ**: Common questions about the platform

## File Structure

```
petcare-frontend/
├── src/
│   ├── components/
│   │   └── AIChatbot.jsx              # Main chatbot component
│   ├── services/
│   │   └── openaiService.js           # OpenAI API integration
│   ├── styles/
│   │   └── AIChatbot.css              # Chatbot styling
│   ├── pages/
│   │   └── Landing.js                 # Updated with chatbot import
│   └── ...
├── .env.example                        # Environment variables template
└── .env                                # Your actual environment variables (Add VITE_OPENAI_API_KEY)
```

## Setup Instructions

### 1️⃣ Get OpenAI API Key

1. Go to [OpenAI Platform](https://platform.openai.com/api-keys)
2. Sign up or log in
3. Create a new API key
4. Copy the key

### 2️⃣ Configure Environment Variables

1. Copy the example file:
   ```bash
   cp .env.example .env
   ```

2. Add your OpenAI API key to `.env`:
   ```env
   VITE_OPENAI_API_KEY=sk_your_actual_api_key_here
   ```

3. Make sure `.env` is in `.gitignore` (do NOT commit your API key)

### 3️⃣ Install Dependencies

If you haven't already installed dependencies:
```bash
npm install
```

### 4️⃣ Start Development Server

```bash
npm run dev
```

Visit `http://localhost:3000` and you should see the chatbot toggle button (💬) in the bottom-right corner.

## Component Documentation

### AIChatbot.jsx

**Main chatbot component with UI and chat logic.**

```javascript
import AIChatbot from '../components/AIChatbot';

// Use in your component:
<AIChatbot />
```

**Features:**
- Toggle open/close with smooth animations
- Message display with distinct styles for user/assistant
- Auto-scroll to latest message
- Loading indicators while waiting for response
- Error message display
- Clean state management with React hooks

**Props:** None (standalone component)

### openaiService.js

**Service module for OpenAI API integration.**

```javascript
import { 
  sendMessage, 
  resetConversationHistory 
} from '../services/openaiService';

// Send a message and get a response
const response = await sendMessage('How do I create an account?');

// Reset conversation history
resetConversationHistory();

// Get conversation history
import { getConversationHistory } from '../services/openaiService';
const history = getConversationHistory();
```

**Key Functions:**

| Function | Description |
|----------|-------------|
| `sendMessage(userMessage)` | Send a message to OpenAI and get response |
| `resetConversationHistory()` | Clear conversation history |
| `getConversationHistory()` | Get current conversation messages |

**System Prompt Features:**
- Defines assistant as a "friendly PetCare virtual assistant"
- Provides context about app features
- Includes mock data for veterinarians
- Guides users through registration
- Encourages appropriate next steps

## Styling

### CSS Architecture

The chatbot uses a modern CSS design with:
- **Gradient backgrounds** (purple/blue theme)
- **Smooth animations** (slide-up, pulse, bounce)
- **Responsive layout** (desktop and mobile)
- **Dark mode support** (optional)
- **Custom scrollbar** styling

### Customization

Edit `src/styles/AIChatbot.css` to:
- Change colors: Update gradient values
- Adjust sizing: Modify `width` and `height` properties
- Modify animations: Update `@keyframes` sections
- Fine-tune positioning: Adjust `bottom` and `right` values

Example color change:
```css
/* From purple/blue to green theme */
background: linear-gradient(135deg, #10b981 0%, #059669 100%);
```

## API Integration

### OpenAI API Call Flow

```
User Message
     ↓
sendMessage() in openaiService.js
     ↓
Fetch to https://api.openai.com/v1/chat/completions
     ↓
GPT-3.5-turbo processes with system prompt
     ↓
Response returned to component
     ↓
Display in chat UI
```

### Request Format

```javascript
{
  model: 'gpt-3.5-turbo',
  messages: [
    {
      role: 'system',
      content: 'System prompt defining assistant behavior...'
    },
    {
      role: 'user',
      content: 'User message...'
    },
    ...conversation history...
  ],
  max_tokens: 150,
  temperature: 0.7
}
```

## Security Considerations

⚠️ **IMPORTANT: Production Deployment**

**Current Implementation:** API key exposed on frontend (for development only)

**Production Recommendation:**
1. **Route through Backend**: Create an endpoint in your Spring Boot backend:
   ```java
   POST /api/chat/completions
   ```
2. **Keep API Key Secure**: Store in backend environment variables
3. **Backend Code Example**:
   ```java
   @PostMapping("/api/chat/completions")
   public ResponseEntity<?> chatCompletion(@RequestBody ChatRequest request) {
       // Call OpenAI with secure API key from Environment variable
       // Return response to frontend
   }
   ```
4. **Frontend Update**: Change `openaiService.js` to call your backend endpoint instead of OpenAI directly

### Never:
- ❌ Commit `.env` file with API key
- ❌ Expose API key in frontend code for production
- ❌ Store API key in localStorage or sessionStorage
- ❌ Log or display the API key anywhere

### Always:
- ✅ Use environment variables
- ✅ Add `.env` to `.gitignore`
- ✅ Route sensitive API calls through backend
- ✅ Rotate API keys regularly

## Troubleshooting

### Chatbot Not Showing

**Problem:** Chat button not visible on Landing page
- ✅ Check that `AIChatbot` is imported in `Landing.js`
- ✅ Verify CSS file is being loaded
- ✅ Check browser console for errors (F12)

### API Key Errors

**Problem:** "OpenAI API key not configured"
- ✅ Add `VITE_OPENAI_API_KEY=sk_...` to `.env`
- ✅ Restart development server
- ✅ Clear browser cache

**Problem:** "Invalid OpenAI API key"
- ✅ Verify key starts with `sk_`
- ✅ Check key is active on OpenAI dashboard
- ✅ Make sure it's not expired or revoked

### Chat Not Responding

**Problem:** Assistant not replying to messages
- ✅ Check API key is valid
- ✅ Check internet connection
- ✅ Look for error messages in browser console
- ✅ Verify OpenAI API status at https://status.openai.com/

### Styling Issues

**Problem:** Chatbot looks broken or misaligned
- ✅ Clear browser cache (Ctrl+Shift+Del)
- ✅ Hard refresh page (Ctrl+F5)
- ✅ Check `.env` file syntax
- ✅ Verify CSS file path in imports

## Performance Optimization

### Token Usage

The chatbot is optimized to minimize API token usage:
- **Max tokens**: 150 per response
- **Temperature**: 0.7 (balanced creativity/consistency)
- **Conversation history**: Maintained in memory (not persisted)

### Cost Estimation

GPT-3.5-turbo pricing (as of 2024):
- Input: $0.50 / 1M tokens
- Output: $1.50 / 1M tokens

Example: 100 users × 5 messages × 200 tokens avg ≈ $0.05

## Future Enhancements

### Potential Features
1. **Backend Integration**: Route API calls through Spring Boot backend
2. **Persistent History**: Save conversations to database
3. **Analytics**: Track common user questions
4. **Multi-language Support**: Support different languages
5. **Custom Styling**: Admin panel to customize chatbot appearance
6. **Appointment Booking**: Suggest booking appointments
7. **Real-time Notifications**: Push notifications for appointment reminders
8. **Sentiment Analysis**: Detect user frustration and escalate to human support

## Support

For issues or questions:
1. Check the troubleshooting section above
2. Review OpenAI API documentation: https://platform.openai.com/docs
3. Check browser console for detailed error messages
4. Verify all files are correctly placed in project structure

## References

- [OpenAI API Documentation](https://platform.openai.com/docs)
- [React Hooks Documentation](https://react.dev/reference/react)
- [Vite Environment Variables](https://vitejs.dev/guide/env-and-modes.html)
- [CSS Animations Guide](https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_Animations)
