import React, { useContext } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../utils/auth';

const Navbar = () => {
  const { isAuthenticated, username, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
      <nav className="navbar navbar-expand-lg navbar-light bg-light">
        <div className="container-fluid">
          <Link className="navbar-brand" to="/">LabOOP API</Link>
          <div className="collapse navbar-collapse">
            <ul className="navbar-nav me-auto mb-2 mb-lg-0">
              {/* Показываем ссылки на функции ТОЛЬКО если пользователь аутентифицирован */}
              {isAuthenticated && (
                <>
                  <li className="nav-item">
                    <Link className="nav-link" to="/functions">Функции</Link>
                  </li>
                  <li className="nav-item">
                    <Link className="nav-link" to="/functions/new">Создать Функцию</Link>
                  </li>
                </>
              )}
            </ul>
            <ul className="navbar-nav">
              {/* Показываем ссылки на вход/регистрацию если НЕ аутентифицирован */}
              {!isAuthenticated ? (
                <>
                  <li className="nav-item">
                    <Link className="nav-link" to="/login">Вход</Link>
                  </li>
                  <li className="nav-item">
                    <Link className="nav-link" to="/register">Регистрация</Link>
                  </li>
                </>
              ) : (
                // Показываем имя пользователя и кнопку выхода если аутентифицирован
                <>
                  <li className="nav-item">
                    <span className="navbar-text me-3">Привет, {username}</span>
                  </li>
                  <li className="nav-item">
                    <button className="btn btn-outline-secondary" onClick={handleLogout}>Выход</button>
                  </li>
                </>
              )}
            </ul>
          </div>
        </div>
      </nav>
    );
  };

export default Navbar;