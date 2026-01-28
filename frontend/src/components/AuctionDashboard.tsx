import { useQuery } from "@apollo/client/react";
import { useState } from "react";
import { GET_AUCTION_PLAYERS } from "../graphql/queries";
import type { GetAuctionPlayersResponse } from "../types/graphql";
import AuctionPlayerList from "./AuctionPlayerList";
import SelectedPlayerPanel from "./SelectedPlayerPanel";

export default function AuctionDashboard() {
  const { data, loading, error } = useQuery<GetAuctionPlayersResponse>(
    GET_AUCTION_PLAYERS,
    {
      variables: { auctionId: "1" },
    },
  );

  const [selectedAuctionPlayerId, setSelectedAuctionPlayerId] = useState<
    string | null
  >(null);

  const selectedAuctionPlayer =
    data?.auctionPlayers.find((ap) => ap.id === selectedAuctionPlayerId) ||
    null;

  if (loading) return <div className="p-6">Loading auction...</div>;
  if (error) return <div className="p-6 text-red-500">{error.message}</div>;
  if (!data) return <div className="p-6">No data available</div>;

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-100 to-slate-200 p-6">
      <h1 className="text-3xl font-bold mb-8">Auction Dashboard</h1>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {/* LEFT: Player List */}
        <div className="md:col-span-1">
          <AuctionPlayerList
            players={data.auctionPlayers}
            selectedId={selectedAuctionPlayerId}
            onSelect={setSelectedAuctionPlayerId}
          />
        </div>

        {/* RIGHT: Selected Player Details */}
        <div className="md:col-span-2">
          <SelectedPlayerPanel auctionPlayer={selectedAuctionPlayer} />
        </div>
      </div>
    </div>
  );
}
