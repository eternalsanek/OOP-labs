import React, { useState, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../utils/auth';

const Register = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const { register } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    const result = await register(username, password);
    if (result.success) {
      setSuccess(result.message);
      setTimeout(() => {
         navigate('/login'); // Перенаправить на вход через 2 секунды
      }, 2000);
    } else {
      setError(result.message);
    }
  };

  return (
    <div className="row justify-content-center">
      <div className="col-md-6">
        <div className="card">
          <div className="card-body">
            <h2 className="card-title text-center">Регистрация</h2>
            {error && <div className="alert alert-danger">{error}</div>}
            {success && <div className="alert alert-success">{success}</div>}
            <form onSubmit={handleSubmit}>
              <div className="mb-3">
                <label htmlFor="regUsername" className="form-label">Имя пользователя</label>
                <input
                  type="text"
                  className="form-control"
                  id="regUsername"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  required
                />
              </div>
              <div className="mb-3">
                <label htmlFor="regPassword" className="form-label">Пароль</label>
                <input
                  type="password"
                  className="form-control"
                  id="regPassword"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                />
              </div>
              <button type="submit" className="btn btn-success w-100">Зарегистрироваться</button>
            </form>
            <div className="mt-3 text-center">
              <p>Уже есть аккаунт? <a href="/login">Войти</a></p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Register;