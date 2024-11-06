import React, { useState, useEffect } from 'react';
import axios from 'axios';
import ProfitCalendar from './ProfitCalendar';

const CalendarApp = () => {
    const [profitData, setProfitData] = useState({});

    useEffect(() => {
        const fetchProfitData = async () => {
            try {
                const response = await axios.get('http://localhost:8080/api/trade-status');
                console.log('API Response:', response.data);
                const transformedData = response.data.statuses.reduce((acc, status) => {
                    acc[status.date] = status.profit;
                    return acc;
                }, {});
                console.log('Transformed Data:', transformedData);
                setProfitData(transformedData);
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
