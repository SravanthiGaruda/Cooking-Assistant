import React, { Component } from 'react';
import axios from 'axios';
import './Chat.css';

class Chat extends Component {
  constructor(props) {
    super(props);
    this.state = {
      messages: [
        { text: "Hello! How can I help you?", isUser: false }
      ],
      userInput: '',
      isRecording: false,
      recognition: null,
    };
  }

  componentDidMount() {
    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
    if (SpeechRecognition) {
      const recognition = new SpeechRecognition();
      recognition.continuous = false;
      recognition.interimResults = false;
      recognition.lang = 'en-US';

      recognition.onresult = (event) => {
        const transcript = event.results[0][0].transcript;
        this.setState({ userInput: transcript }, this.handleSubmit);
      };

      recognition.onerror = (event) => {
        console.error('Speech recognition error', event.error);
      };

      this.setState({ recognition });
    } else {
      console.error('Speech recognition not supported in this browser');
    }
  }

  handleChange = (e) => {
    this.setState({ userInput: e.target.value });
  }

  handleSubmit = async (e) => {
    if (e) e.preventDefault();
    const { userInput } = this.state;
    if (!userInput) return;

    try {
      // Send message to the backend
      const response = await axios.post('http://localhost:8080/send-message', { message: userInput });
      const { data } = response;

      // Update state with the sent message and the response from the backend
      this.setState(prevState => ({
        messages: [
          ...prevState.messages,
          { text: userInput, isUser: true }, // Add user's message
          { text: data, isUser: false }      // Add response from the backend
        ],
        userInput: '' // Clear the userInput state
      }));

      // Generate speech from backend TTS API
      await this.generateSpeech(data);
    } catch (error) {
      console.error('Error sending message:', error);
      // Handle errors, such as displaying an error message to the user
    }
  }

  generateSpeech = async (text, language = 'en', speed = 175, pitch = 50) => {
    try {
        const response = await axios.post('http://localhost:8080/send-message/tts', { text, language, speed, pitch }, { responseType: 'blob' });
        const audioUrl = URL.createObjectURL(new Blob([response.data], { type: 'audio/wav' }));
        const audio = new Audio(audioUrl);
        audio.play();
    } catch (error) {
        console.error('Error generating speech:', error);
        // Handle errors
    }
}


  startRecording = () => {
    const { recognition } = this.state;
    if (recognition) {
      recognition.start();
      this.setState({ isRecording: true });
    }
  }

  stopRecording = () => {
    const { recognition } = this.state;
    if (recognition) {
      recognition.stop();
      this.setState({ isRecording: false });
    }
  }

  render() {
    const { messages, userInput, isRecording } = this.state;
    return (
      <div className="chat-container">
        <h1 className="chat-title">Cooking Assistant</h1>
        <div className="messages">
          {messages.map((msg, index) => (
            <div key={index} className={`message ${msg.isUser ? 'user-message' : 'bot-message'}`}>
              {msg.text}
            </div>
          ))}
        </div>
        <form onSubmit={this.handleSubmit} className="input-form">
          <input
            type="text"
            value={userInput}
            onChange={this.handleChange}
            placeholder="Type your message..."
            className="input-field"
          />
          <button type="submit" className="send-button">Send</button>
        </form>
        <button
          onClick={isRecording ? this.stopRecording : this.startRecording}
          className="record-button"
        >
          {isRecording ? 'Stop Recording' : 'Start Recording'}
        </button>
      </div>
    );
  }
}

export default Chat;