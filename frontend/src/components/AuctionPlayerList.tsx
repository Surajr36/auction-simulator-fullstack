import type { AuctionPlayer } from "../types/graphql";

type Props = {
  players: AuctionPlayer[];
  selectedId: string | null;
  onSelect: (id: string) => void;
};

export default function AuctionPlayerList({
  players,
  selectedId,
  onSelect,
}: Props) {
  return (
    <div className="space-y-2">
      {players.map((ap) => {
        const isSelected = ap.id === selectedId;

        return (
          <li
            key={ap.id}
            onClick={() => onSelect(ap.id)}
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
                <span className="inline-block mt-1 px-2 py-0.5 text-xs rounded font-semibold bg-slate-200 text-slate-800">
                  {ap.player.category}
                </span>
              </div>

              <div className="text-right">
                <div className="font-medium">₹ {ap.currentPrice} cr</div>
                <span className="mt-1 inline-block px-2 py-0.5 text-xs rounded font-medium bg-gray-200 text-gray-700">
                  {ap.status}
                </span>
              </div>
            </div>
          </li>
        );
      })}
    </div>
  );
}
