export const transformExpenditureData = (data) => {
  if (!data || typeof data !== "object")
    return { name: "Expenditure", children: [] };

  return {
    name: "Expenditure",
    children: Object.entries(data).map(([description, amount]) => ({
      name: description || "Unknown",
      value: !isNaN(amount) ? amount : 0,
    })),
  };
};
