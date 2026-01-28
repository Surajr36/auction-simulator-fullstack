import { useMutation, useQuery } from "@apollo/client/react";
import type {
  AuctionPlayer,
  GetBidsResponse,
  PlaceBidResponse,
} from "../types/graphql";
import { GET_BIDS_FOR_AUCTION_PLAYER, PLACE_BID } from "../graphql/queries";
import { useState } from "react";

type Props = {
  auctionPlayer: AuctionPlayer | null;
};

export default function SelectedPlayerPanel({ auctionPlayer }: Props) {
  const auctionPlayerId = auctionPlayer?.id;

  const { data, loading, error } = useQuery<GetBidsResponse>(
    GET_BIDS_FOR_AUCTION_PLAYER,
    {
      variables: auctionPlayerId ? { auctionPlayerId } : undefined,
      skip: !auctionPlayer,
    },
  );

  const [placeBid, { loading: placingBid, error: bidError }] =
    useMutation<PlaceBidResponse>(PLACE_BID, {
      refetchQueries: [
        {
          query: GET_BIDS_FOR_AUCTION_PLAYER,
          variables: auctionPlayerId ? { auctionPlayerId } : undefined,
        },
      ],
    });

  // hooks must be called unconditionally and in the same order
  const [bidAmount, setBidAmount] = useState<number | null>(null);
  const [bidSuccess, setBidSuccess] = useState(false);

  if (!auctionPlayer) {
    return (
      <div className="bg-gray-50 rounded-xl p-6 text-gray-500">
        Select a player to view details
      </div>
    );
  }

  const effectiveBidAmount =
    bidAmount === null ? auctionPlayer.currentPrice : bidAmount;

  const incrementBid = (value: number) => {
    setBidAmount((prev) =>
      Number(((prev ?? auctionPlayer.currentPrice) + value).toFixed(1)),
    );
  };

  const handlePlaceBid = async () => {
    try {
      await placeBid({
        variables: {
          auctionPlayerId: auctionPlayer.id,
          amount: effectiveBidAmount,
        },
      });

      setBidAmount(null);
      setBidSuccess(true);

      setTimeout(() => {
        setBidSuccess(false);
      }, 2000);
    } catch {
      // Apollo handles error
    }
  };

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

        <hr className="my-6" />

        <button
          onClick={handlePlaceBid}
          disabled={
            placingBid ||
            effectiveBidAmount <= auctionPlayer.currentPrice ||
            auctionPlayer.status !== "LIVE"
          }
          className="w-full py-2 rounded font-semibold
    disabled:bg-gray-300 disabled:cursor-not-allowed
    bg-green-500 hover:bg-green-600 text-white"
        >
          {placingBid ? "Placing Bid..." : "Place Bid"}
        </button>
        {bidError && (
          <div className="text-red-500 text-sm mt-2">{bidError.message}</div>
        )}
        {bidSuccess && (
          <div className="text-green-600 text-sm mt-2 font-medium">
            Bid placed successfully
          </div>
        )}

        <div className="space-y-4">
          <div className="text-3xl font-bold text-green-600 transition-all">
            ₹ {auctionPlayer.currentPrice} cr
          </div>

          <div className="flex gap-2">
            <button
              disabled={auctionPlayer.status !== "LIVE"}
              onClick={() => incrementBid(0.2)}
              className="px-3 py-1 rounded font-medium
    disabled:bg-gray-200 disabled:text-gray-400
    bg-blue-100 hover:bg-blue-200"
            >
              +0.2
            </button>

            <button
              disabled={auctionPlayer.status !== "LIVE"}
              onClick={() => incrementBid(0.5)}
              className="px-3 py-1 rounded font-medium
    disabled:bg-gray-200 disabled:text-gray-400
    bg-blue-100 hover:bg-blue-200"
            >
              +0.5
            </button>
          </div>

          <div>
            <div className="text-sm text-gray-500">Bid Amount</div>
            <div className="text-2xl font-bold text-blue-700">
              ₹ {effectiveBidAmount} cr
            </div>
          </div>
        </div>

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
