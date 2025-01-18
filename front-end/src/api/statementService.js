import axios from "axios";

// Set the backend base URL explicitly
const API_BASE_URL = "http://localhost:8080/api/statements";

const StatementService = {
  calculateExpenditure: (type) =>
    axios.get(`${API_BASE_URL}/expenditure`, { params: { type } }),
};

export default StatementService;
