import React, { useState } from 'react';

const DummyBoomroom: React.FC = () => {
  const [userInput, setUserInput] = useState('');
  const [response, setResponse] = useState('');

  const dummyResponses = [
    "Jaaaaaaaa luisteraars! Wat een heerlijke vraag weer!",
    "Ik zou zeggen... KNALLEN maar!",
    "Dat is echt Alkemeniaans gezegd, waanzinnig!",
    "Mensen, dit wordt weer een CRAZY aflevering!",
  ];

  const handleAskAlkemade = () => {
    const randomResponse = dummyResponses[Math.floor(Math.random() * dummyResponses.length)];
    setResponse(randomResponse);
  };

  return (
    <div className="min-h-screen bg-gradient-to-r from-purple-900 to-black text-white">
      <div className="container mx-auto px-4 py-8">
        {/* Header */}
        <div className="text-center mb-12">
          <h1 className="text-6xl font-bold mb-4">The Boom Room AI</h1>
          <p className="text-xl">Powered by Alkemeniaans Intelligence</p>
        </div>

        {/* Stats Dashboard */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-12">
          <StatCard title="Episodes" value="1,900+" />
          <StatCard title="Total Streams" value="2.5M+" />
          <StatCard title="Guest DJs" value="500+" />
        </div>

        {/* Alkemeniaans Chat Interface */}
        <div className="max-w-2xl mx-auto bg-gray-900 rounded-lg p-6">
          <h2 className="text-2xl font-bold mb-4">Vraag het aan Gijs</h2>
          <div className="space-y-4">
            <input
              type="text"
              value={userInput}
              onChange={(e) => setUserInput(e.target.value)}
              className="w-full p-3 rounded bg-gray-800 text-white"
              placeholder="Stel je vraag aan Gijs..."
            />
            <button
              onClick={handleAskAlkemade}
              className="w-full bg-purple-600 hover:bg-purple-700 py-3 rounded font-bold"
            >
              KNALLEN MAAR!
            </button>
            {response && (
              <div className="mt-4 p-4 bg-gray-800 rounded">
                <p className="italic">"{response}"</p>
              </div>
            )}
          </div>
        </div>

        {/* Trending Topics */}
        <div className="mt-12">
          <h2 className="text-2xl font-bold mb-4">Trending in The Boom Room</h2>
          <div className="flex flex-wrap gap-2">
            {["#Techno", "#Underground", "#Amsterdam", "#ADE", "#DanceMusic"].map((tag) => (
              <span key={tag} className="bg-purple-800 px-3 py-1 rounded-full text-sm">
                {tag}
              </span>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

const StatCard: React.FC<{ title: string; value: string }> = ({ title, value }) => (
  <div className="bg-gray-900 p-6 rounded-lg text-center">
    <h3 className="text-xl mb-2">{title}</h3>
    <p className="text-3xl font-bold text-purple-400">{value}</p>
  </div>
);

export default DummyBoomroom; 