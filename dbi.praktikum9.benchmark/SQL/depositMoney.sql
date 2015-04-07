-- Function: depositmoney(integer, integer, integer, integer, character)

-- DROP FUNCTION depositmoney(integer, integer, integer, integer, character);

CREATE OR REPLACE FUNCTION depositmoney(paccountid integer, ptellerid integer, pbranchid integer, pdelta integer, pcomment character)
  RETURNS integer AS
$BODY$
DECLARE newBalance integer;
BEGIN
UPDATE branches 
SET balance = balance + pDelta
WHERE branchid = pBranchID;

UPDATE tellers 
SET balance = balance + pDelta
WHERE tellerid = pTellerID;

UPDATE accounts 
SET balance = balance + pDelta
WHERE accid = pAccountID;

SELECT balance INTO newBalance
FROM accounts
WHERE accid = pAccountID;

INSERT INTO history(accid, tellerid, delta, branchid, accbalance, cmmnt)
VALUES (pAccountID, pTellerID, pDelta, pBranchID, newBalance - pDelta, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ1234');

RETURN newBalance;
END
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION depositmoney(integer, integer, integer, integer, character)
  OWNER TO dbi;
