"use client";

type DatePickerProps = {
  label: string;
  value: string;
  onChange: (value: string) => void;
};

const DatePicker: React.FC<DatePickerProps> = ({ label, value, onChange }) => {
  // Calculate tomorrow's date in the format 'YYYY-MM-DD'
  const tomorrow = new Date();
  tomorrow.setDate(tomorrow.getDate() + 1);
  const minDate = tomorrow.toISOString().split("T")[0];

  return (
    <div>
      <label className="block mb-1 font-medium">{label}</label>
      <input
        type="date"
        value={value}
        onChange={(e) => onChange(e.target.value)}
        min={minDate} // Disable today and all earlier dates
        className="border rounded p-2 w-full"
      />
    </div>
  );
};

export default DatePicker;