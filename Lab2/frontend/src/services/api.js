import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080'; // URL вашего Spring Boot приложения

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Перехватчик для добавления токена к каждому запросу
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Перехватчик для обработки ошибок
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Если получили 401, возможно, токен истек, очищаем данные
      localStorage.removeItem('token');
      localStorage.removeItem('username');
      window.location.href = '/login'; // Перенаправляем на страницу входа
    }
    // Можно здесь показать глобальный алерт
    alert(error.response?.data?.message || 'Произошла ошибка');
    return Promise.reject(error);
  }
);

export default api;