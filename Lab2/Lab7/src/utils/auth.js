import React, { createContext, useState, useContext } from 'react';
import axios from '../services/api'; // Используем наш API сервис

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState(() => {
    // Проверяем наличие токена в localStorage при загрузке
    return !!localStorage.getItem('token');
  });
  const [username, setUsername] = useState(() => {
    // Получаем имя пользователя из localStorage при загрузке
    return localStorage.getItem('username') || '';
  });

  const login = async (username, password) => {
    try {
      const response = await axios.post('/api/v1/users/login', {
        username,
        password
      });
      if (response.status === 200) {
        const { token, username: returnedUsername } = response.data;
        localStorage.setItem('token', token);
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
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    setIsAuthenticated(false);
    setUsername('');
  };

  return (
    <AuthContext.Provider value={{ isAuthenticated, username, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);