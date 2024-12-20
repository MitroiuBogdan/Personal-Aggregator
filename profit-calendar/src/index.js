import React from 'react';
import { createRoot } from 'react-dom/client';
import './index.css';
import CalendarApp from './CalendarApp';

const container = document.getElementById('root');
const root = createRoot(container); // createRoot(container!) if you use TypeScript
root.render(
    <React.StrictMode>
        <CalendarApp />
    </React.StrictMode>
);
