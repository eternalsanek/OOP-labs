import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom'; // Добавлен Link
import api from '../../services/api';
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer
} from 'recharts';

const FunctionPlot = () => {
  const { id } = useParams(); // Получаем ID функции из URL
  const [data, setData] = useState([]);
  const [functionData, setFunctionData] = useState(null); // Добавлено состояние для данных функции
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchFunctionPoints = async () => {
      try {
        const response = await api.get(`/api/v1/functions/${id}`);
        setFunctionData(response.data); // Сохраняем данные функции

        const chartData = (response.data.points || []).map(point => ({
          name: Number(point.xVal),
          x: Number(point.xVal),
          y: Number(point.yVal)
        }));
        setData(chartData);
      } catch (err) {
        setError(err.response?.data?.message || 'Ошибка при загрузке данных для графика');
        console.error('Error fetching plot ', err);
      } finally {
        setLoading(false);
      }
    };

    fetchFunctionPoints();
  }, [id]);

  if (loading) return <div>Загрузка графика...</div>;
  if (error) return <div className="alert alert-danger">Ошибка: {error}</div>;

  return (
    <div>
      {/* Заголовок с именем функции */}
      <h2>График Функции: {functionData?.name || 'Загрузка...'}</h2>

      {/* Кнопки навигации */}
      <div className="mb-3"> {/* Добавлен контейнер для кнопок */}
        {/* Кнопка Редактировать функцию */}
        <Link to={`/functions/${id}/edit`} className="btn btn-warning me-2"> {/* Используем id функции */}
          Редактировать функцию
        </Link>
        {/* Кнопка Назад к списку */}
        <Link to="/functions" className="btn btn-secondary"> {/* Путь к списку функций */}
          Назад к списку
        </Link>
      </div>

      {/* Компонент графика */}
      <ResponsiveContainer width="100%" height={400}>
        <LineChart
          data={data}
          margin={{
            top: 5,
            right: 30,
            left: 20,
            bottom: 5,
          }}
        >
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="name" label={{ value: 'X', position: 'insideBottomRight', offset: -5 }} />
          <YAxis label={{ value: 'Y', angle: -90, position: 'insideLeft' }} />
          <Tooltip />
          <Legend />
          <Line
            type="monotone"
            dataKey="y"
            name="Значение Y"
            stroke="#8884d8"
            activeDot={{ r: 8 }}
          />
        </LineChart>
      </ResponsiveContainer>
    </div>
  );
};

export default FunctionPlot;