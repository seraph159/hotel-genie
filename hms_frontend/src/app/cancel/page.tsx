import Link from 'next/link';

const CancelPage = () => {
    return (
      <div style={{ textAlign: "center", margin: "20px" }}>
        <h1>Payment Canceled 😔</h1>
        <p>Your booking was not completed.</p>
        <p>If you have any questions, please contact our support team.</p>
        <Link href="/" passHref style={{ color: "blue", textDecoration: "underline" }}>
          Return to Home
        </Link>
      </div>
    );
  };
  
export default CancelPage;
  