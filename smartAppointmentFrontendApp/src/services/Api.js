// services/api.js
import axios from 'axios';

const BASE_URL = 'https://2cfc-2401-4900-22a7-d206-10b7-2256-9c4b-d183.ngrok-free.app/api'; 

const api = axios.create({
  baseURL: BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const loginUser = async (email, password) => {
  const response = await api.post('/login', {
    email,
    password,
  });
  return response.data;
};

export default api;
