import axios from 'axios';
import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import {
  CartesianGrid,
  Legend,
  Line,
  LineChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts';
import UserNavbar from './UserNavbar';

const Diagram = () => {
  const [viewCountData, setViewCountData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const { id } = useParams();

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await axios.get(`http://localhost:8080/recipeenter/navigation/${id}`, {
          headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
        });

        if (response.data) {
          const views = parseInt(response.data.split('Views: ')[1].split(',')[0], 10);
          const date = response.data.split('Generated Date: ')[1];
          setViewCountData([{ date, viewCount: views }]);
        }
      } catch (err) {
        setError('Failed to fetch data.');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [id]);

  if (loading) {
    return <div className="text-center text-primary mt-5">Loading data...</div>;
  }

  if (error) {
    return <div className="text-center text-danger mt-5">{error}</div>;
  }

  return (
    <div>
      <UserNavbar />
      <div
        style={{
          backgroundColor: '#f8f9fa',
          border: '1px solid #dee2e6',
          borderRadius: '8px',
          padding: '20px',
          margin: '20px',
          boxShadow: '0px 4px 6px rgba(0, 0, 0, 0.1)',
        }}
      >
        <h3 className="text-center text-primary mb-4">View Count Over Time</h3>
        <ResponsiveContainer width="100%" height={400}>
          <LineChart data={viewCountData}>
            <CartesianGrid strokeDasharray="3 3" stroke="#e0e0e0" />
            <XAxis dataKey="date" tick={{ fontSize: 12, fill: '#6c757d' }} />
            <YAxis tick={{ fontSize: 12, fill: '#6c757d' }} />
            <Tooltip
              contentStyle={{
                backgroundColor: '#ffffff',
                border: '1px solidrgb(233, 165, 149)',
                borderRadius: '8px',
              }}
              itemStyle={{ color: '#0d6efd' }}
            />
            <Legend
              verticalAlign="bottom"
              wrapperStyle={{ bottom: 0, fontSize: 12, color: '#495057' }}
            />
            <Line
              type="monotone"
              dataKey="viewCount"
              stroke="url(#colorUv)"
              strokeWidth={3}
              name="View Count"
              dot={{ r: 6 }}
              activeDot={{ r: 8 }}
            />
            <defs>
              <linearGradient id="colorUv" x1="0" y1="0" x2="0" y2="1">
                <stop offset="5%" stopColor="#0d6efd" stopOpacity={0.8} />
                <stop offset="95%" stopColor="#0d6efd" stopOpacity={0.2} />
              </linearGradient>
            </defs>
          </LineChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
};

export default Diagram;
