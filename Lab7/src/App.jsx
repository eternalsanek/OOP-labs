import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Navbar from './components/Layout/Navbar';
import Login from './components/Auth/Login';
import Register from './components/Auth/Register';
import FunctionList from './components/Functions/FunctionList';
import FunctionForm from './components/Functions/FunctionForm';
import FunctionEditor from './components/Functions/FunctionEditor';
import FunctionPlot from './components/Functions/FunctionPlot';
import Home from './components/Home/Home';
import { AuthProvider } from './utils/auth'; // Предполагаем контекст авторизации
import './App.css';

function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="App">
          <Navbar />
          <main>
            <Routes>
              {/* Главная страница */}
              <Route path="/" element={<Home />} />
              {/* Страница входа */}
              <Route path="/login" element={<Login />} />
              {/* Страница регистрации */}
              <Route path="/register" element={<Register />} />
              {/* Страница списка функций (теперь по /functions) */}
              <Route path="/functions" element={<FunctionList />} />
              {/* Страница создания функции */}
              <Route path="/functions/new" element={<FunctionForm />} />
              {/* Страница редактирования функции */}
              <Route path="/functions/:id/edit" element={<FunctionEditor />} />
              {/* Страница просмотра графика функции */}
              <Route path="/functions/:id/plot" element={<FunctionPlot />} />
            </Routes>
          </main>
        </div>
      </Router>
    </AuthProvider>
  );
}

export default App;