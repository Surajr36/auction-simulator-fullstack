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

export const GET_BIDS_FOR_AUCTION_PLAYER = gql`
  query GetBidsForAuctionPlayer($auctionPlayerId: ID!) {
    bids(auctionPlayerId: $auctionPlayerId) {
      id
      amount
      createdAt
      team {
        id
        name
      }
    }
  }
`;

export const PLACE_BID = gql`
  mutation PlaceBid($auctionPlayerId: ID!, $amount: Float!) {
    placeBid(auctionPlayerId: $auctionPlayerId, amount: $amount) {
      id
      currentPrice
      status
      currentHighestBidTeam {
        id
        name
      }
    }
  }
`;
