import React, { createContext, useState, useContext } from 'react';
import axios from '../services/api'; // Используем наш API сервис

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  // Для Basic Auth, если используем сессии, проверяем аутентификацию по другим признакам
  // или просто доверяем состоянию приложения после успешного входа.
  // Проверяем наличие имени пользователя в localStorage как признака входа
  // (можно хранить и другие признаки, например, флаг isAuthenticated).
  const [isAuthenticated, setIsAuthenticated] = useState(() => !!localStorage.getItem('username'));
  // Получаем имя пользователя из localStorage при загрузке
  const [username, setUsername] = useState(() => localStorage.getItem('username') || '');

  const login = async (username, password) => {
    try {
      // Отправляем запрос на вход
      const response = await axios.post('/api/v1/users/login', {
        username,
        password
      });

      if (response.status === 200) {
        // Ответ не содержит токена при Basic Auth
        // const { token, username: returnedUsername } = response.data; // УБРАНО
        const { username: returnedUsername } = response.data; // Берём имя из ответа

        // Сохраняем имя пользователя в localStorage как признак входа
        // (можно также сохранить флаг isAuthenticated: true)
        localStorage.setItem('username', returnedUsername);
        setIsAuthenticated(true);
        setUsername(returnedUsername);
        return { success: true };
      }
    } catch (error) {
      console.error('Login error:', error);
      return { success: false, message: error.response?.data?.message || 'Ошибка входа' };
    }
  };

  const register = async (username, password) => {
    try {
      // Отправляем запрос на регистрацию
      const response = await axios.post('/api/v1/users/register', {
        username,
        password
      });

      if (response.status === 200) {
        return { success: true, message: 'Пользователь успешно зарегистрирован' };
      }
    } catch (error) {
      console.error('Registration error:', error);
      return { success: false, message: error.response?.data?.message || 'Ошибка регистрации' };
    }
  };

  const logout = () => {
    // Очищаем данные аутентификации
    // localStorage.removeItem('token'); // УБРАНО
    localStorage.removeItem('username');
    setIsAuthenticated(false);
    setUsername('');
    // Важно: при Basic Auth + сессии, возможно, нужно сделать вызов на logout endpoint,
    // чтобы сервер удалил сессию. Добавьте такой endpoint в Spring Boot, если нужно.
    // axios.post('/api/v1/users/logout'); // Пример, если endpoint реализован.
  };

  return (
    <AuthContext.Provider value={{ isAuthenticated, username, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);