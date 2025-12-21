import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../utils/auth'; // Импортируем хук для проверки состояния аутентификации

const Home = () => {
  const { isAuthenticated } = useAuth(); // Получаем состояние аутентификации

  return (
    <div className="container mt-5">
      <div className="row justify-content-center">
        <div className="col-md-8 text-center">
          <h1 className="display-4">Добро пожаловать в LabOOP API</h1>
          <p className="lead">
            Это приложение позволяет управлять математическими функциями и их точками.
          </p>
          {isAuthenticated ? (
            // Если пользователь аутентифицирован, предложим перейти к функциям
            <div>
              <p>Вы вошли в систему. Перейдите к списку ваших функций.</p>
              <Link className="btn btn-primary btn-lg" to="/functions"> {/* Предполагаем, что '/' теперь список функций */}
                Мои Функции
              </Link>
            </div>
          ) : (
            // Если пользователь НЕ аутентифицирован, покажем кнопки входа/регистрации
            <div>
              <p>Пожалуйста, войдите или зарегистрируйтесь, чтобы продолжить.</p>
              <Link className="btn btn-primary btn-lg me-2" to="/login">
                Войти
              </Link>
              <Link className="btn btn-success btn-lg" to="/register">
                Регистрация
              </Link>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Home;