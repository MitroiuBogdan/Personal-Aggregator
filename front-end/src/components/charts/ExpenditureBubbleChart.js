import React, { useRef } from "react";
import useFetchExpenditure from "../../hooks/useFetchExpenditure";
import useBubbleChart from "../../hooks/useBubbleChart";

// Chart dimensions and styles
const CHART_DIMENSIONS = {
  width: "600px",
  height: "500px",
};

const CHART_CONTAINER_STYLE = {
  ...CHART_DIMENSIONS,
  margin: "0 auto",
  border: "1px solid #ddd",
};

const ExpenditureBubbleChart = () => {
  const { data, loading, error } = useFetchExpenditure("CARD_PAYMENT");
  const chartRef = useRef();

  // Use Bubble Chart Hook
  useBubbleChart(data, chartRef);

  // Conditional Rendering
  if (loading) return <div>Loading data...</div>;
  if (error) return <div>Error fetching data: {error.message}</div>;
  if (!data || !data.children || !Array.isArray(data.children)) {
    console.error("Invalid data structure for Bubble Chart:", data);
    return <div>Invalid data structure.</div>;
  }

  return (
    <div ref={chartRef} style={CHART_CONTAINER_STYLE}>
      <h2 style={{ textAlign: "center" }}>Expenditure Dashboard</h2>
    </div>
  );
};

export default ExpenditureBubbleChart;
