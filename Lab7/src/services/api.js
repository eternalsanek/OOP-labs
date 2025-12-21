import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080'; // URL вашего Spring Boot приложения

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true
});

// Перехватчик для обработки ошибок
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Если получили 401, очищаем данные аутентификации (если хранятся)
      // localStorage.removeItem('token'); // УБРАНО
      localStorage.removeItem('username'); // Оставим, если имя пользователя хранится отдельно
      // Перенаправляем на страницу входа
      window.location.href = '/login';
    }
    // Показываем глобальный алерт
    alert(error.response?.data?.message || 'Произошла ошибка');
    return Promise.reject(error);
  }
);

export default api;