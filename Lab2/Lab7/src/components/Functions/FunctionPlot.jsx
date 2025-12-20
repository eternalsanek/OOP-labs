import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
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
  const { id } = useParams();
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchFunctionPoints = async () => {
      try {
        const response = await api.get(`/api/v1/functions/${id}`); // Получаем полную информацию о функции
        // Предполагаем, что backend возвращает точки в формате, подходящем для Recharts
        // Например, response.data.points = [{x: val, y: val}, ...]
        // Recharts ожидает {name: x_val, value: y_val} или просто {x: x_val, y: y_val}
        // Адаптируем, если нужно:
        const chartData = (response.data.points || []).map(point => ({
          name: point.x, // Для подписи на оси X
          x: point.x,
          y: point.y
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
      <h2>График Функции</h2>
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