import React, { useState, useEffect } from 'react';
import axios from 'axios';
import ProfitCalendar from './ProfitCalendar';

const CalendarApp = () => {
    const [profitData, setProfitData] = useState({});

    useEffect(() => {
        const fetchProfitData = async () => {
            try {
                const response = await axios.get('https://api.example.com/profits');
                setProfitData(response.data);
            } catch (error) {
                console.error('Error fetching profit data:', error);
            }
        };

        fetchProfitData();
    }, []);

    return (
        <div>
            <h1>Profit Calendar</h1>
            <ProfitCalendar profitData={profitData} />
        </div>
    );
};

export default CalendarApp;
