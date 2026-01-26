import { gql } from "@apollo/client";

export const GET_AUCTION_PLAYERS = gql`
  query GetAuctionPlayers($auctionId: ID!) {
    auctionPlayers(auctionId: $auctionId) {
      id
      status
      basePrice
      currentPrice
      player {
        id
        name
        category
      }
      currentHighestBidTeam {
        id
        name
      }
    }
  }
`;
