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

  const selectedAuctionPlayer = data?.auctionPlayers.find(
    (ap) => ap.id === selectedAuctionPlayerId,
  );

  if (loading) return <div className="p-6">Loading auction...</div>;
  if (error) return <div className="p-6 text-red-500">{error.message}</div>;

  return (
    /* CHANGE: added full-page background gradient for better visual feel */
    <div className="min-h-screen bg-gradient-to-br from-slate-100 to-slate-200 p-6">
      <h1 className="text-3xl font-bold mb-8">Auction Dashboard</h1>

      {/* CHANGE: unified layout — list on left, details on right */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {/* LEFT: Auction Player List */}
        <div className="md:col-span-1 space-y-2">
          {data?.auctionPlayers.map((ap) => {
            const isSelected = ap.id === selectedAuctionPlayerId;

            return (
              <li
                key={ap.id}
                onClick={() => setSelectedAuctionPlayerId(ap.id)}
                /* CHANGE: stronger visual feedback + hover + transition */
                className={`list-none p-4 rounded-lg cursor-pointer transition-all
                  ${
                    isSelected
                      ? "bg-blue-600 text-white shadow-lg scale-[1.02]"
                      : "bg-white hover:bg-blue-50"
                  }
                `}
              >
                <div className="flex justify-between items-center">
                  <div>
                    <div className="font-semibold">{ap.player.name}</div>

                    {/* CHANGE: category shown as a colored badge */}
                    <span
                      className={`inline-block mt-1 px-2 py-0.5 text-xs rounded font-semibold
                        ${
                          ap.player.category === "BAT"
                            ? "bg-purple-200 text-purple-800"
                            : ap.player.category === "BOWL"
                              ? "bg-indigo-200 text-indigo-800"
                              : ap.player.category === "AR"
                                ? "bg-orange-200 text-orange-800"
                                : "bg-pink-200 text-pink-800"
                        }
                      `}
                    >
                      {ap.player.category}
                    </span>
                  </div>

                  <div className="text-right">
                    <div className="font-medium">₹ {ap.currentPrice} cr</div>

                    {/* CHANGE: clearer status semantics */}
                    <span
                      className={`mt-1 inline-block px-2 py-0.5 text-xs rounded font-medium
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
        </div>

        {/* RIGHT: Selected Player Details */}
        <div className="md:col-span-2">
          {selectedAuctionPlayer ? (
            /* CHANGE: upgraded details panel styling */
            <div className="bg-white rounded-xl shadow-xl p-6">
              <h2 className="text-2xl font-bold mb-2">
                {selectedAuctionPlayer.player.name}
              </h2>

              <div className="text-gray-600 mb-4">
                {selectedAuctionPlayer.player.category}
              </div>

              <hr className="my-4" />

              <div className="space-y-3">
                <div>
                  <span className="font-medium">Base Price:</span> ₹{" "}
                  {selectedAuctionPlayer.basePrice} cr
                </div>

                {/* CHANGE: emphasize current price */}
                <div className="text-2xl font-bold text-green-600">
                  ₹ {selectedAuctionPlayer.currentPrice} cr
                </div>

                <div>
                  <span className="font-medium">Status:</span>{" "}
                  {selectedAuctionPlayer.status}
                </div>

                {selectedAuctionPlayer.currentHighestBidTeam && (
                  <div>
                    <span className="font-medium">Leading Team:</span>{" "}
                    {selectedAuctionPlayer.currentHighestBidTeam.name}
                  </div>
                )}
              </div>
            </div>
          ) : (
            <div className="bg-gray-50 rounded-xl p-6 text-gray-500">
              Select a player to view details
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default App;
