import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import api from '../../services/api';

const FunctionList = () => {
  const [functions, setFunctions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchFunctions = async () => {
      try {
        const response = await api.get('/api/v1/functions/my'); // Предполагаемый эндпоинт
        setFunctions(response.data);
      } catch (err) {
        setError(err.message);
        console.error('Error fetching functions:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchFunctions();
  }, []);

const handleDeleteFunction = async (id) => {
    if (!window.confirm('Вы уверены, что хотите удалить эту функцию?')) {
      return; // Прерываем выполнение, если пользователь отменил
    }

    try {
      await api.delete(`/api/v1/functions/${id}`); // Отправляем DELETE-запрос
      // Обновляем список функций в состоянии, убрав удалённую
      setFunctions(prevFunctions => prevFunctions.filter(func => func.id !== id));
      alert('Функция успешно удалена!');
    } catch (err) {
      setError(err.response?.data?.message || 'Ошибка при удалении функции');
      console.error('Error deleting function:', err);
    }
  };

  if (loading) return <div>Загрузка функций...</div>;
  if (error) return <div className="alert alert-danger">Ошибка: {error}</div>;

  return (
    <div>
      <h2>Мои Функции</h2>
      <Link to="/functions/new" className="btn btn-primary mb-3">Создать новую функцию</Link>
      {functions.length === 0 ? (
        <p>У вас пока нет сохранённых функций.</p>
      ) : (
        <ul className="list-group">
          {functions.map((func) => (
            <li key={func.id} className="list-group-item d-flex justify-content-between align-items-center">
              <div>
                <strong>{func.name}</strong> ({func.type})
              </div>
              <div>
                <Link to={`/functions/${func.id}/plot`} className="btn btn-sm btn-info me-2">График</Link>
                <Link to={`/functions/${func.id}/edit`} className="btn btn-sm btn-warning me-2">Редактировать</Link>
                {/* Кнопка удаления */}<button className="btn btn-sm btn-danger me-2" onClick={() => handleDeleteFunction(func.id)}>Удалить</button>
              </div>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default FunctionList;