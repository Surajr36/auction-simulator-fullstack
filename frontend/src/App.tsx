import { useQuery } from "@apollo/client/react";
import { GET_AUCTION_PLAYERS } from "./graphql/queries";
import { useState } from "react";
import type { GetAuctionPlayersResponse } from "./types/graphql";

function App() {
  const { data, loading, error } = useQuery<GetAuctionPlayersResponse>(
    GET_AUCTION_PLAYERS,
    {
      variables: { auctionId: "1" },
    },
  );
  const [selectedAuctionPlayerId, setSelectedAuctionPlayerId] = useState<
    string | null
  >(null);
  console.log("Selected:", selectedAuctionPlayerId);

  if (loading) return <div className="p-6">Loading auction...</div>;
  if (error) return <div className="p-6 text-red-500">{error.message}</div>;

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-4">Auction Players</h1>

      <ul className="space-y-2">
        {data?.auctionPlayers.map((ap) => {
          const isSelected = ap.id === selectedAuctionPlayerId;

          return (
            <li
              key={ap.id}
              onClick={() => setSelectedAuctionPlayerId(ap.id)}
              className={`p-4 border rounded cursor-pointer
              ${isSelected ? "bg-blue-200 border-blue-600 ring-2 ring-blue-400" : "bg-white"}
            `}
            >
              <div className="flex justify-between items-center">
                <div>
                  <div className="font-semibold">{ap.player.name}</div>
                  <div className="text-sm text-gray-600">
                    {ap.player.category}
                  </div>
                </div>

                <div className="text-right">
                  <div>₹ {ap.currentPrice} cr</div>
                  <span
                    className={`px-2 py-1 text-xs rounded font-medium
    ${
      ap.status === "LIVE"
        ? "bg-green-200 text-green-800"
        : ap.status === "NOT_STARTED"
          ? "bg-gray-200 text-gray-700"
          : "bg-red-200 text-red-800"
    }
  `}
                  >
                    {ap.status}
                  </span>
                </div>
              </div>
            </li>
          );
        })}
      </ul>
    </div>
  );
}

export default App;
