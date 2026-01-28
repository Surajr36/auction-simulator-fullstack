import { useQuery } from "@apollo/client/react";
import type { AuctionPlayer, GetBidsResponse } from "../types/graphql";
import { GET_BIDS_FOR_AUCTION_PLAYER } from "../graphql/queries";

type Props = {
  auctionPlayer: AuctionPlayer | null;
};

export default function SelectedPlayerPanel({ auctionPlayer }: Props) {
  const { data, loading, error } = useQuery<GetBidsResponse>(
    GET_BIDS_FOR_AUCTION_PLAYER,
    {
      variables: { auctionPlayerId: auctionPlayer?.id },
      skip: !auctionPlayer,
    },
  );

  if (!auctionPlayer) {
    return (
      <div className="bg-gray-50 rounded-xl p-6 text-gray-500">
        Select a player to view details
      </div>
    );
  }

  return (
    <div className="bg-white rounded-xl shadow-xl p-6">
      <h2 className="text-2xl font-bold mb-2">{auctionPlayer.player.name}</h2>

      <div className="text-gray-600 mb-4">{auctionPlayer.player.category}</div>

      <hr className="my-4" />

      <div className="space-y-3">
        <div>
          <span className="font-medium">Base Price:</span> ₹{" "}
          {auctionPlayer.basePrice} cr
        </div>

        <div className="text-2xl font-bold text-green-600">
          ₹ {auctionPlayer.currentPrice} cr
        </div>

        <div>
          <span className="font-medium">Status:</span> {auctionPlayer.status}
        </div>

        {auctionPlayer.currentHighestBidTeam && (
          <div>
            <span className="font-medium">Leading Team:</span>{" "}
            {auctionPlayer.currentHighestBidTeam.name}
          </div>
        )}
        <hr className="my-6" />

        <h3 className="text-lg font-semibold mb-2">Bid History</h3>

        {loading && <div className="text-gray-500">Loading bids...</div>}

        {error && (
          <div className="text-red-500 text-sm">Failed to load bids</div>
        )}

        {data && data.bids.length === 0 && (
          <div className="text-gray-500 text-sm">No bids yet</div>
        )}

        {data && data.bids.length > 0 && (
          <ul className="space-y-2">
            {data.bids.map((bid) => (
              <li
                key={bid.id}
                className="flex justify-between items-center p-2 bg-gray-50 rounded"
              >
                <div>
                  <div className="font-medium">{bid.team.name}</div>
                  <div className="text-xs text-gray-500">
                    {new Date(bid.createdAt).toLocaleTimeString()}
                  </div>
                </div>

                <div className="font-semibold">₹ {bid.amount} cr</div>
              </li>
            ))}
          </ul>
        )}
      </div>
    </div>
  );
}
