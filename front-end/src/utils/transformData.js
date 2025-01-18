export const transformExpenditureData = (data) => {
  if (!data || typeof data !== "object" || !Array.isArray(data.children)) {
    console.error("Invalid data structure:", data);
    return { name: "Expenditure", children: [] };
  }

  return {
    name: data.name || "Expenditure",
    children: data.children
      .map((item) => ({
        name: item?.name?.trim() || "Unknown",
        value:
          typeof item.value === "number" ? Number(item.value.toFixed(2)) : 0,
      }))
      .filter((item) => item.value > 0), // Remove invalid or zero values
  };
};
