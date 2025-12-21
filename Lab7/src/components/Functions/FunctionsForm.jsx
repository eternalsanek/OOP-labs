import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../services/api';

const FunctionForm = () => {
  const [name, setName] = useState('');
  const [type, setType] = useState('ArrayTabulatedFunction'); // Значение по умолчанию
  const [pointsInput, setPointsInput] = useState(''); // Ввод точек в формате "x1,y1;x2,y2;..."
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    // Парсим строки ввода точек в массив объектов
    let points = [];
    try {
      const pairs = pointsInput.split(';').filter(p => p.trim());
      points = pairs.map(pair => {
        const [x, y] = pair.split(',').map(Number);
        if (isNaN(x) || isNaN(y)) throw new Error(`Неверный формат точки: ${pair}`);
        return { x, y };
      });
    } catch (parseError) {
      setError(parseError.message);
      return;
    }

    // Подготовка данных для отправки
    // Важно: сохранение НЕ поточечно! Отправляем массив точек.
    const functionData = {
      name,
      type, // Тип функции (например, ArrayTabulatedFunction)
      points, // Массив точек
    };

    try {
      await api.post('/api/v1/functions', functionData);
      alert('Функция успешно создана!');
      navigate('/'); // Перенаправить на список функций
    } catch (err) {
      setError(err.response?.data?.message || 'Ошибка при создании функции');
      console.error('Error creating function:', err);
    }
  };

  return (
    <div>
      <h2>Создать Новую Функцию</h2>
      {error && <div className="alert alert-danger">{error}</div>}
      <form onSubmit={handleSubmit}>
        <div className="mb-3">
          <label htmlFor="functionName" className="form-label">Имя функции</label>
          <input
            type="text"
            className="form-control"
            id="functionName"
            value={name}
            onChange={(e) => setName(e.target.value)}
            required
          />
        </div>
        <div className="mb-3">
          <label htmlFor="functionType" className="form-label">Тип функции</label>
          <select
            className="form-control"
            id="functionType"
            value={type}
            onChange={(e) => setType(e.target.value)}
          >
            <option value="ArrayTabulatedFunction">ArrayTabulatedFunction</option>
            <option value="LinkedListTabulatedFunction">LinkedListTabulatedFunction</option>
            {/* Добавьте другие типы, если нужно */}
          </select>
        </div>
        <div className="mb-3">
          <label htmlFor="pointsInput" className="form-label">Точки (формат: x1,y1;x2,y2;...)</label>
          <textarea
            className="form-control"
            id="pointsInput"
            rows="4"
            value={pointsInput}
            onChange={(e) => setPointsInput(e.target.value)}
            placeholder="Например: 0,0; 1,1; 2,4; 3,9"
            required
          ></textarea>
        </div>
        <button type="submit" className="btn btn-success">Создать Функцию</button>
      </form>
    </div>
  );
};

export default FunctionForm;