import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Navbar from './components/Layout/Navbar';
import Login from './components/Auth/Login';
import Register from './components/Auth/Register';
import FunctionList from './components/Functions/FunctionList';
import FunctionForm from './components/Functions/FunctionForm';
import FunctionEditor from './components/Functions/FunctionEditor';
import FunctionPlot from './components/Functions/FunctionPlot';
import { AuthProvider } from './utils/auth'; // Предполагаем контекст авторизации

function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="App">
          <Navbar />
          <main className="container mt-4">
            <Routes>
              <Route path="/login" element={<Login />} />
              <Route path="/register" element={<Register />} />
              <Route path="/" element={<FunctionList />} /> {/* Путь по умолчанию */}
              <Route path="/functions" element={<FunctionList />} />
              <Route path="/functions/new" element={<FunctionForm />} />
              <Route path="/functions/:id/edit" element={<FunctionEditor />} />
              <Route path="/functions/:id/plot" element={<FunctionPlot />} />
            </Routes>
          </main>
        </div>
      </Router>
    </AuthProvider>
  );
}

export default App;