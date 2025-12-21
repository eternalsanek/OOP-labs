import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import api from '../../services/api';

const FunctionEditor = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [functionData, setFunctionData] = useState(null);
  const [name, setName] = useState('');
  const [newPointX, setNewPointX] = useState('');
  const [newPointY, setNewPointY] = useState('');
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchFunction = async () => {
      try {
        const response = await api.get(`/api/v1/functions/${id}`);
        setFunctionData(response.data);
        setName(response.data.name);
      } catch (err) {
        setError(err.response?.data?.message || 'Ошибка при загрузке функции');
        console.error('Error fetching function:', err);
      }
    };

    fetchFunction();
  }, [id]);

  const handleSaveName = async () => {
    if (!name.trim()) {
      setError('Имя функции не может быть пустым');
      return;
    }
    try {
      await api.patch(`/api/v1/functions/${id}`, { name }); // Предполагаем PATCH для обновления имени
      setFunctionData(prev => ({ ...prev, name }));
      alert('Имя функции обновлено!');
    } catch (err) {
      setError(err.response?.data?.message || 'Ошибка при сохранении имени');
      console.error('Error saving name:', err);
    }
  };

  const handleAddPoint = async () => {
    const x = parseFloat(newPointX);
    const y = parseFloat(newPointY);
    if (isNaN(x) || isNaN(y)) {
      setError('Введите корректные числовые значения X и Y');
      return;
    }

    try {
       // Предполагаем, что эндпоинт для добавления точки к функции существует
      await api.post(`/api/v1/functions/${id}/points`, { x, y });
      // Обновляем локально, или перезагружаем данные
      setFunctionData(prev => {
        if (prev) {
           // Добавляем точку в список, предполагая, что структура такая
           // Это зависит от того, как backend возвращает и обрабатывает точки
           // Здесь просто пример, может потребоваться адаптация
           const updatedPoints = [...(prev.points || []), { x, y }];
           return { ...prev, points: updatedPoints };
        }
        return prev;
      });
      setNewPointX('');
      setNewPointY('');
      alert('Точка добавлена!');
    } catch (err) {
      setError(err.response?.data?.message || 'Ошибка при добавлении точки');
      console.error('Error adding point:', err);
    }
  };

  const handleDeletePoint = async (pointIndex) => {
    // Удаление точки по индексу - зависит от API бэкенда
    // Предположим, что есть эндпоинт DELETE /api/v1/functions/{id}/points/{index}
    try {
      await api.delete(`/api/v1/functions/${id}/points/${pointIndex}`);
      // Обновляем локально
      setFunctionData(prev => {
        if (prev && prev.points) {
          const updatedPoints = [...prev.points];
          updatedPoints.splice(pointIndex, 1);
          return { ...prev, points: updatedPoints };
        }
        return prev;
      });
      alert('Точка удалена!');
    } catch (err) {
      setError(err.response?.data?.message || 'Ошибка при удалении точки');
      console.error('Error deleting point:', err);
    }
  };

  if (error) return <div className="alert alert-danger">Ошибка: {error}</div>;
  if (!functionData) return <div>Загрузка...</div>;

  return (
    <div>
      <h2>Редактировать Функцию: {functionData.name}</h2>
      <div className="mb-3">
        <label htmlFor="editName" className="form-label">Изменить имя:</label>
        <div className="input-group">
          <input
            type="text"
            className="form-control"
            id="editName"
            value={name}
            onChange={(e) => setName(e.target.value)}
          />
          <button className="btn btn-outline-primary" type="button" onClick={handleSaveName}>Сохранить Имя</button>
        </div>
      </div>

      <h3>Точки функции</h3>
      {functionData.points && functionData.points.length > 0 ? (
        <table className="table table-striped">
          <thead>
            <tr>
              <th scope="col">X</th>
              <th scope="col">Y</th>
              <th scope="col">Действия</th>
            </tr>
          </thead>
          <tbody>
            {functionData.points.map((point, index) => (
              <tr key={index}>
                <td>{point.x}</td>
                <td>{point.y}</td>
                <td>
                  <button
                    className="btn btn-sm btn-danger"
                    onClick={() => handleDeletePoint(index)}
                  >
                    Удалить
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      ) : (
        <p>Нет точек.</p>
      )}

      <h4>Добавить новую точку</h4>
      <div className="mb-3 input-group">
        <input
          type="number"
          className="form-control"
          placeholder="X"
          value={newPointX}
          onChange={(e) => setNewPointX(e.target.value)}
        />
        <input
          type="number"
          className="form-control"
          placeholder="Y"
          value={newPointY}
          onChange={(e) => setNewPointY(e.target.value)}
        />
        <button className="btn btn-outline-success" type="button" onClick={handleAddPoint}>
          Добавить Точку
        </button>
      </div>

      <Link to={`/functions/${id}/plot`} className="btn btn-info me-2">Посмотреть График</Link>
      <Link to="/" className="btn btn-secondary">Назад к списку</Link>
    </div>
  );
};

export default FunctionEditor;