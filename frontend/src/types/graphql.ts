export type Player = {
  id: string;
  name: string;
  category: "BAT" | "BOWL" | "AR" | "WKB";
};

export type Team = {
  id: string;
  name: string;
};

export type AuctionPlayer = {
  id: string;
  status: "LIVE" | "NOT_STARTED" | "SOLD";
  basePrice: number;
  currentPrice: number;
  player: Player;
  currentHighestBidTeam: Team | null;
};

export type GetAuctionPlayersResponse = {
  auctionPlayers: AuctionPlayer[];
};

export type Bid = {
  id: string;
  amount: number;
  createdAt: string;
  team: {
    id: string;
    name: string;
  };
};

export type GetBidsResponse = {
  bids: Bid[];
};
