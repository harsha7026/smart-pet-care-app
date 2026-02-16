# PetCare AI Chatbot - Quick Start Guide

## ⚡ Get Started in 5 Minutes

### Step 1: Get Your OpenAI API Key (2 minutes)

1. Go to: https://platform.openai.com/api-keys
2. Click "Create new secret key"
3. Copy the key (starts with `sk_`)
4. **SAVE IT SAFELY** - You won't see it again

### Step 2: Configure Environment (1 minute)

1. Open `petcare-frontend/.env`
2. Add this line (replace with your actual key):
   ```env
   VITE_OPENAI_API_KEY=sk_your_key_here
   ```
3. Save the file

### Step 3: Start Your App (1 minute)

```bash
cd petcare-frontend
npm install
npm run dev
```

### Step 4: Test the Chatbot (1 minute)

1. Open http://localhost:3000
2. Look for the 💬 button in the bottom-right corner
3. Click it to open the chat
4. Try asking: "How do I create an account?"

## 🎯 What You Just Added

✨ **Floating AI Chatbot**
- Appears on the Landing Page only
- Helps visitors learn about the app
- Powered by OpenAI GPT

## 📁 Files Created

```
src/
├── components/
│   └── AIChatbot.jsx              ← Main chatbot component
├── services/
│   └── openaiService.js           ← OpenAI integration
└── styles/
    └── AIChatbot.css              ← Chatbot styling

pages/
└── Landing.js                     ← Updated with chatbot

.env                               ← Add VITE_OPENAI_API_KEY here
```

## ❓ Common Questions

**Q: Where does the chatbot appear?**
A: Bottom-right corner of the Landing Page only

**Q: Can I customize the chatbot?**
A: Yes! See `src/styles/AIChatbot.css` for styling or `src/services/openaiService.js` for behavior

**Q: Is my API key safe?**
A: Not in frontend. For production, route API calls through your backend (see guide)

**Q: Does it work offline?**
A: No, it requires internet connection to call OpenAI API

**Q: How much does it cost?**
A: Very cheap (~$0.001 per message with GPT-3.5-turbo)

## 🐛 Not Working?

1. **Check API Key**
   ```bash
   # .env file should have:
   VITE_OPENAI_API_KEY=sk_xxxxxxxx
   ```

2. **Restart dev server**
   ```bash
   # Stop with Ctrl+C, then:
   npm run dev
   ```

3. **Check browser console** (F12 → Console tab)

4. **Verify OpenAI API key is active** at https://platform.openai.com/api-keys

## 📚 Full Documentation

See `CHATBOT_IMPLEMENTATION_GUIDE.md` for complete details on:
- Architecture
- Customization
- Production deployment
- Advanced features
- Troubleshooting

## 🚀 Next Steps

1. **Test with real users** - Get feedback on chatbot responses
2. **Monitor costs** - Track OpenAI API usage
3. **Customize answers** - Edit system prompt in `openaiService.js`
4. **Move to production** - Route API through backend (see guide)
5. **Add features** - Persistent history, analytics, etc.

## 📞 Support

If you hit issues:
1. Check the FAQ above
2. Review full guide: `CHATBOT_IMPLEMENTATION_GUIDE.md`
3. Check OpenAI status: https://status.openai.com
4. Review browser console errors (F12)

---

**That's it! Your chatbot is ready to help visitors learn about PetCare.** 🎉
